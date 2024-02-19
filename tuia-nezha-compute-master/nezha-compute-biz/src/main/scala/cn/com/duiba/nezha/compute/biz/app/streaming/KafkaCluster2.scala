package cn.com.duiba.nezha.compute.biz.app.streaming

import cn.com.duiba.nezha.compute.biz.app.streaming.KafkaCluster.SimpleConsumerConfig
import kafka.api.OffsetCommitRequest
import kafka.common.{OffsetAndMetadata, ErrorMapping, TopicAndPartition}
import kafka.consumer.SimpleConsumer
import org.apache.spark.SparkException

import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import scala.util.control.NonFatal

/**
 * Created by pc on 2017/7/25.
 */
class KafkaCluster2(val kafkaParams: Map[String, String]) extends Serializable {
  type Err = ArrayBuffer[Throwable]

  @transient private var _config: SimpleConsumerConfig = null

  def config: SimpleConsumerConfig = this.synchronized {
    if (_config == null) {
      _config = SimpleConsumerConfig(kafkaParams)
    }
    _config
  }

  def setConsumerOffsets(
                          groupId: String,
                          offsets: Map[TopicAndPartition, Long],
                          consumerApiVersion: Short
                          ): Either[Err, Map[TopicAndPartition, Short]] = {
    val meta = offsets.map { kv =>
      kv._1 -> OffsetAndMetadata(kv._2)
    }
    setConsumerOffsetMetadata(groupId, meta, consumerApiVersion)
  }

  def setConsumerOffsetMetadata(
                                 groupId: String,
                                 metadata: Map[TopicAndPartition, OffsetAndMetadata],
                                 consumerApiVersion: Short
                                 ): Either[Err, Map[TopicAndPartition, Short]] = {
    var result = Map[TopicAndPartition, Short]()
    val req = OffsetCommitRequest(groupId, metadata, consumerApiVersion)
    val errs = new Err
    val topicAndPartitions = metadata.keySet
    withBrokers(Random.shuffle(config.seedBrokers), errs) { consumer =>
      val resp = consumer.commitOffsets(req)
      val respMap = resp.commitStatus
      val needed = topicAndPartitions.diff(result.keySet)
      needed.foreach { tp: TopicAndPartition =>
        respMap.get(tp).foreach { err: Short =>
          if (err == ErrorMapping.NoError) {
            result += tp -> err
          } else {
            errs.append(ErrorMapping.exceptionFor(err))
          }
        }
      }
      if (result.keys.size == topicAndPartitions.size) {
        return Right(result)
      }
    }
    val missing = topicAndPartitions.diff(result.keySet)
    errs.append(new SparkException(s"Couldn't set offsets for ${missing}"))
    Left(errs)
  }

  private def withBrokers(brokers: Iterable[(String, Int)], errs: Err)
                         (fn: SimpleConsumer => Any): Unit = {
    brokers.foreach { hp =>
      var consumer: SimpleConsumer = null
      try {
        consumer = connect(hp._1, hp._2)
        fn(consumer)
      } catch {
        case NonFatal(e) =>
          errs.append(e)
      } finally {
        if (consumer != null) {
          consumer.close()
        }
      }
    }
  }

  def connect(host: String, port: Int): SimpleConsumer =
    new SimpleConsumer(host, port, config.socketTimeoutMs,
      config.socketReceiveBufferBytes, config.clientId)
}