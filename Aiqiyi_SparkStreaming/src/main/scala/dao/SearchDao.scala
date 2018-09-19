package dao

import caseclass.Search
import org.apache.hadoop.hbase.util.Bytes
import utils.HBaseUtil

import scala.collection.mutable.ListBuffer

object SearchDao {

  val tableName = "search"
  val cf = "info"
  val column = "searchCount"

  def save(list: ListBuffer[Search]): Unit = {
    val table = HBaseUtil.getTable(tableName)
    for(ele <- list) {
      table.incrementColumnValue(Bytes.toBytes(ele.searchId),Bytes.toBytes(cf),Bytes.toBytes(column),ele.searchCount)
    }
  }
}

