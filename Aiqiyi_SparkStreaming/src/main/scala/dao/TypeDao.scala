package dao

import caseclass.Type
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes
import utils.HBaseUtil
import scala.collection.mutable.ListBuffer

object TypeDao {

  val tableName = "type"
  val cf = "info"
  val column = "typeCount"

  def save(list:ListBuffer[Type]): Unit ={
    val table =  HBaseUtil.getTable(tableName)
    for(ele <- list){
      table.incrementColumnValue(Bytes.toBytes(ele.typeId),Bytes.toBytes(cf),Bytes.toBytes(column),ele.typeCount)
    }
  }

  def count(typeId:String) : Long={
    val table  =HBaseUtil.getTable(tableName)
    val get = new Get(Bytes.toBytes(typeId))
    val  value =  table.get(get).getValue(Bytes.toBytes(cf), Bytes.toBytes(column))
    if(value == null){
      0L
    }else{
      Bytes.toLong(value)
    }
  }

  def main(args: Array[String]): Unit = {
    val list = new ListBuffer[Type]
    list.append(Type("20171122_8",20))
    list.append(Type("20171122_9", 40))
    list.append(Type("20171122_10", 60))
    save(list)
    print(count("20171122_8")+"-->"+count("20171122_9")+"-->"+count("20171122_10"))
    // 第一次：20-->40-->60
    // 第二次：40-->80-->120
  }

}
