package utils

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{HTable, Put}
import org.apache.hadoop.hbase.util.Bytes

object HBaseUtil {

  def getConf() = {
    var conf: Configuration = null
    conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum", "mini1:2181")
    conf.set("hbase.rootdir", "hdfs://mini1:9000/hbase")
    conf
  }

  def getTable(tableName: String): HTable = {
    var table: HTable = null
    try {
      table = new HTable(getConf(), tableName)
    }
    catch {
      case e: Exception => e.printStackTrace()
    }
    table
  }

  def put(tableName: String, rowKey: String, cf: String, col: String, value: Long): Unit = {
    var table = getTable(tableName)
    var put = new Put(Bytes.toBytes(rowKey))
    put.add(Bytes.toBytes(cf), Bytes.toBytes(col), Bytes.toBytes(value))
    try {
      table.put(put)
    }
    catch {
      case e: Exception => e.printStackTrace()
    }
  }

  def main(args: Array[String]): Unit = {
    val tableName = "type"
    val rowkey = "20180919_2"
    val cf = "info"
    val colum = "typeCount"
    val value = 592
    HBaseUtil.put(tableName, rowkey, cf, colum, value)
  }
}

