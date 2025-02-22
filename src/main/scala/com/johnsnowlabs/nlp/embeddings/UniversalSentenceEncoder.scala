/*
 * Copyright 2017-2022 John Snow Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.johnsnowlabs.nlp.embeddings

import com.johnsnowlabs.ml.tensorflow.{ReadTensorflowModel, TensorflowUSE, TensorflowWrapper, WriteTensorflowModel}
import com.johnsnowlabs.nlp.AnnotatorType.{DOCUMENT, SENTENCE_EMBEDDINGS}
import com.johnsnowlabs.nlp.annotators.common.SentenceSplit
import com.johnsnowlabs.nlp.{Annotation, AnnotatorModel, HasPretrained, ParamsAndFeaturesReadable, HasSimpleAnnotate}
import com.johnsnowlabs.storage.HasStorageRef

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.ml.param.{BooleanParam, IntArrayParam, IntParam}
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.io.File

/**
 * The Universal Sentence Encoder encodes text into high dimensional vectors that can be used for text classification, semantic similarity, clustering and other natural language tasks.
 *
 * Pretrained models can be loaded with `pretrained` of the companion object:
 * {{{
 * val useEmbeddings = UniversalSentenceEncoder.pretrained()
 *   .setInputCols("sentence")
 *   .setOutputCol("sentence_embeddings")
 * }}}
 * The default model is `"tfhub_use"`, if no name is provided.
 * For available pretrained models please see the [[https://nlp.johnsnowlabs.com/models?task=Embeddings Models Hub]].
 *
 * For extended examples of usage, see the [[https://github.com/JohnSnowLabs/spark-nlp-workshop/blob/master/tutorials/Certification_Trainings/Public/3.SparkNLP_Pretrained_Models.ipynb Spark NLP Workshop]]
 * and the [[https://github.com/JohnSnowLabs/spark-nlp/blob/master/src/test/scala/com/johnsnowlabs/nlp/embeddings/UniversalSentenceEncoderTestSpec.scala UniversalSentenceEncoderTestSpec]].
 *
 * '''Sources:'''
 *
 * [[https://arxiv.org/abs/1803.11175 Universal Sentence Encoder]]
 *
 * [[https://tfhub.dev/google/universal-sentence-encoder/2]]
 *
 * '''Paper abstract:'''
 *
 * ''We present models for encoding sentences into embedding vectors that specifically target transfer learning to other
 * NLP tasks. The models are efficient and result in accurate performance on diverse transfer tasks. Two variants of the
 * encoding models allow for trade-offs between accuracy and compute resources. For both variants, we investigate and
 * report the relationship between model complexity, resource consumption, the availability of transfer task training
 * data, and task performance. Comparisons are made with baselines that use word level transfer learning via pretrained
 * word embeddings as well as baselines do not use any transfer learning. We find that transfer learning using sentence
 * embeddings tends to outperform word level transfer. With transfer learning via sentence embeddings, we observe
 * surprisingly good performance with minimal amounts of supervised training data for a transfer task. We obtain
 * encouraging results on Word Embedding Association Tests (WEAT) targeted at detecting model bias. Our pre-trained
 * sentence encoding models are made freely available for download and on TF Hub.''
 *
 * ==Example==
 * {{{
 * import spark.implicits._
 * import com.johnsnowlabs.nlp.base.DocumentAssembler
 * import com.johnsnowlabs.nlp.annotator.SentenceDetector
 * import com.johnsnowlabs.nlp.embeddings.UniversalSentenceEncoder
 * import com.johnsnowlabs.nlp.EmbeddingsFinisher
 * import org.apache.spark.ml.Pipeline
 *
 * val documentAssembler = new DocumentAssembler()
 *   .setInputCol("text")
 *   .setOutputCol("document")
 *
 * val sentence = new SentenceDetector()
 *   .setInputCols("document")
 *   .setOutputCol("sentence")
 *
 * val embeddings = UniversalSentenceEncoder.pretrained()
 *   .setInputCols("sentence")
 *   .setOutputCol("sentence_embeddings")
 *
 * val embeddingsFinisher = new EmbeddingsFinisher()
 *   .setInputCols("sentence_embeddings")
 *   .setOutputCols("finished_embeddings")
 *   .setOutputAsVector(true)
 *   .setCleanAnnotations(false)
 *
 * val pipeline = new Pipeline()
 *   .setStages(Array(
 *     documentAssembler,
 *     sentence,
 *     embeddings,
 *     embeddingsFinisher
 *   ))
 *
 * val data = Seq("This is a sentence.").toDF("text")
 * val result = pipeline.fit(data).transform(data)
 *
 * result.selectExpr("explode(finished_embeddings) as result").show(5, 80)
 * +--------------------------------------------------------------------------------+
 * |                                                                          result|
 * +--------------------------------------------------------------------------------+
 * |[0.04616805538535118,0.022307956591248512,-0.044395286589860916,-0.0016493503...|
 * +--------------------------------------------------------------------------------+
 * }}}
 *
 * @see [[https://nlp.johnsnowlabs.com/docs/en/annotators Annotators Main Page]] for a list of transformer based embeddings
 * @groupname anno Annotator types
 * @groupdesc anno Required input and expected output annotator types
 * @groupname Ungrouped Members
 * @groupname param Parameters
 * @groupname setParam Parameter setters
 * @groupname getParam Parameter getters
 * @groupname Ungrouped Members
 * @groupprio param  1
 * @groupprio anno  2
 * @groupprio Ungrouped 3
 * @groupprio setParam  4
 * @groupprio getParam  5
 * @groupdesc param A list of (hyper-)parameter keys this annotator can take. Users can set and get the parameter values through setters and getters, respectively.
 */
class UniversalSentenceEncoder(override val uid: String)
  extends AnnotatorModel[UniversalSentenceEncoder] with HasSimpleAnnotate[UniversalSentenceEncoder]
    with HasEmbeddingsProperties
    with HasStorageRef
    with WriteTensorflowModel {

  /** Annotator reference id. Used to identify elements in metadata or to refer to this annotator type */
  def this() = this(Identifiable.randomUID("UNIVERSAL_SENTENCE_ENCODER"))

  /** Output annotator type : SENTENCE_EMBEDDINGS
   *
   * @group anno
   * */
  override val outputAnnotatorType: AnnotatorType = SENTENCE_EMBEDDINGS
  /** Input annotator type : DOCUMENT
   *
   * @group anno
   * */
  override val inputAnnotatorTypes: Array[AnnotatorType] = Array(DOCUMENT)
  /** Number of embedding dimensions (Default: `512`)
   *
   * @group param
   * */
  override val dimension = new IntParam(this, "dimension", "Number of embedding dimensions")
  /** ConfigProto from tensorflow, serialized into byte array. Get with config_proto.SerializeToString()
   *
   * @group param
   * */
  val configProtoBytes = new IntArrayParam(this, "configProtoBytes", "ConfigProto from tensorflow, serialized into byte array. Get with config_proto.SerializeToString()")

  /** Whether to load SentencePiece ops file which is required only by multi-lingual models (Default: `false`).
   * This is not changeable after it's set with a pretrained model nor it is compatible with Windows.
   *
   * @group param
   */
  val loadSP = new BooleanParam(this, "loadSP", "Whether to load SentencePiece ops file which is required only by multi-lingual models. " +
    "This is not changeable after it's set with a pretrained model nor it is compatible with Windows.")

  /** Whether to load SentencePiece ops file which is required only by multi-lingual models.
   *
   * @group setParam
   * */
  def setLoadSP(value: Boolean): this.type = {
    if (get(loadSP).isEmpty)
      set(this.loadSP, value)
    this
  }

  /** Whether to load SentencePiece ops file which is required only by multi-lingual models.
   *
   * @group getParam
   * */

  def getLoadSP: Boolean = $(loadSP)

  /** ConfigProto from tensorflow, serialized into byte array. Get with config_proto.SerializeToString()
   *
   * @group setParam
   * */
  def setConfigProtoBytes(
                           bytes: Array[Int]
                         ): UniversalSentenceEncoder.this.type = set(this.configProtoBytes, bytes)

  /** ConfigProto from tensorflow, serialized into byte array. Get with config_proto.SerializeToString()
   *
   * @group getParam
   * */
  def getConfigProtoBytes: Option[Array[Byte]] =
    get(this.configProtoBytes).map(_.map(_.toByte))

  private var _model: Option[Broadcast[TensorflowUSE]] = None

  /** @group getParam */
  def getModelIfNotSet: TensorflowUSE = _model.get.value

  /** @group setParam */
  def setModelIfNotSet(spark: SparkSession, tensorflow: TensorflowWrapper): this.type = {
    if (_model.isEmpty) {

      _model = Some(
        spark.sparkContext.broadcast(
          new TensorflowUSE(tensorflow, configProtoBytes = getConfigProtoBytes, loadSP = getLoadSP)
        )
      )
    }
    this
  }

  setDefault(
    dimension -> 512,
    storageRef -> "tfhub_use",
    loadSP -> false
  )

  /**
   * Takes a document and annotations and produces new annotations of this annotator's annotation type
   *
   * @param annotations Annotations that correspond to inputAnnotationCols generated by previous annotators if any
   * @return any number of annotations processed for every input annotation. Not necessary one to one relationship
   */
  override def annotate(annotations: Seq[Annotation]): Seq[Annotation] = {
    val sentences = SentenceSplit.unpack(annotations)
    val nonEmptySentences = sentences.filter(_.content.nonEmpty)

    if (nonEmptySentences.nonEmpty)
      getModelIfNotSet.predict(nonEmptySentences)
    else Seq.empty[Annotation]
  }

  override protected def afterAnnotate(dataset: DataFrame): DataFrame = {
    dataset.withColumn(
      getOutputCol,
      wrapSentenceEmbeddingsMetadata(dataset.col(getOutputCol), $(dimension), Some($(storageRef)))
    )
  }

  override def onWrite(path: String, spark: SparkSession): Unit = {
    super.onWrite(path, spark)
    writeTensorflowModelV2(
      path,
      spark,
      getModelIfNotSet.tensorflow,
      "_use",
      UniversalSentenceEncoder.tfFile,
      configProtoBytes = getConfigProtoBytes
    )
  }

}

trait ReadablePretrainedUSEModel
  extends ParamsAndFeaturesReadable[UniversalSentenceEncoder]
    with HasPretrained[UniversalSentenceEncoder] {
  override val defaultModelName: Some[String] = Some("tfhub_use")

  /** Java compliant-overrides */
  override def pretrained(): UniversalSentenceEncoder = super.pretrained()

  override def pretrained(name: String): UniversalSentenceEncoder = super.pretrained(name)

  override def pretrained(name: String, lang: String): UniversalSentenceEncoder = super.pretrained(name, lang)

  override def pretrained(name: String, lang: String, remoteLoc: String): UniversalSentenceEncoder =
    super.pretrained(name, lang, remoteLoc)
}

trait ReadUSETensorflowModel extends ReadTensorflowModel {
  this: ParamsAndFeaturesReadable[UniversalSentenceEncoder] =>

  /*Needs to point to an actual folder rather than a .pb file*/
  override val tfFile: String = "use_tensorflow"

  def readTensorflow(instance: UniversalSentenceEncoder, path: String, spark: SparkSession): Unit = {
    val loadSP = instance.getLoadSP
    val tf = readTensorflowWithSPModel(path, spark, "_use_tf", initAllTables = true, loadSP = loadSP)
    instance.setModelIfNotSet(spark, tf)
  }

  addReader(readTensorflow)

  def loadSavedModel(folder: String, spark: SparkSession, loadSP: Boolean = false): UniversalSentenceEncoder = {
    val f = new File(folder)
    val savedModel = new File(folder, "saved_model.pb")

    require(f.exists, s"Folder $folder not found")
    require(f.isDirectory, s"File $folder is not folder")
    require(
      savedModel.exists(),
      s"savedModel file saved_model.pb not found in folder $folder"
    )

    val wrapper =
      TensorflowWrapper.readWithSP(folder, zipped = false, useBundle = true, tags = Array("serve"), initAllTables = true, loadSP = loadSP)

    new UniversalSentenceEncoder()
      .setLoadSP(loadSP)
      .setModelIfNotSet(spark, wrapper)
  }
}

/**
 * This is the companion object of [[UniversalSentenceEncoder]]. Please refer to that class for the documentation.
 */
object UniversalSentenceEncoder extends ReadablePretrainedUSEModel with ReadUSETensorflowModel
