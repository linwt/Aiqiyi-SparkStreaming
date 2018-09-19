package main

import caseclass._
import dao.{SearchDao, TypeDao}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import utils.DateUtil

import scala.collection.mutable.ListBuffer

object StreamingApp {

  def main(args: Array[String]): Unit = {
    val ssc = new StreamingContext("local[*]", "StreamingApp", Seconds(5))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "mini1:9092,mini1:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "test",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("flumeTopic")
    val logs = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    ).map(_.value())
//    logs.print()
    //156.187.29.132   2017-11-20 00:39:26	 "GET www/2 HTTP/1.0"	 https://search.yahoo.com/search?p=猎场  	200

    // 清洗日志
    val cleanLog = logs.map(line => {
      val splits = line.split("\t")
      val ip = splits(0)
      val time = DateUtil.parse(splits(1))
      val str = splits(2).split(" ")(1)
      var id = 0
      if(str.startsWith("www")) {
        id = str.split("/")(1).toInt
      }
      val url = splits(3)
      val statusCode = splits(4).toInt
      ClickLog(ip, time, id, url, statusCode)
    }).filter(log => log.id != 0)
//    cleanLog.print()
    // ClickLog(187.29.10.124,20180917,6,https://search.yahoo.com/search?p=猎场,404)
    // ClickLog(124.143.132.30,20180917,4,-,302)

    // 统计每个类别访问量，并保存到数据库
    cleanLog.map(log => {
      (log.time+"_"+log.id, 1)
      // (20180917_6, 1)
    }).reduceByKey(_+_).foreachRDD(rdd => {
      rdd.foreachPartition(partition => {
        val list = new ListBuffer[Type]
        partition.foreach(record => {
          list.append(Type(record._1, record._2))
        })
        TypeDao.save(list)
      })
    })

    // 统计从搜索引擎引流过来的类别的访问量，并保存到数据库
    cleanLog.map(log => {
      val url = log.url.replace("//", "/")
      val splits = url.split("/")
      var host = ""
      if(splits.length > 2){
        host = splits(1)
      }
      (host, log.time, log.id)
      // (search.yahoo.com,20180917,6)
    }).filter(x => x._1 != "").map(x =>
      (x._2+"_"+x._1+"_"+x._3, 1)
      // (20180917_search.yahoo.com_6, 1)
    ).reduceByKey(_+_).foreachRDD(rdd => {
      rdd.foreachPartition(partition => {
        val list = new ListBuffer[Search]
        partition.foreach(record => {
          list.append(Search(record._1, record._2))
        })
        SearchDao.save(list)
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
