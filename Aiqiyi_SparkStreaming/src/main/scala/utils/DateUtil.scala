package utils

import java.util.Date
import org.apache.commons.lang3.time.FastDateFormat

object DateUtil {

  val source = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss")
  val target = FastDateFormat.getInstance("yyyyMMdd")

  def parse(time: String): String = {
    return target.format(new Date(getTime(time)))
  }

  def getTime(time: String) = {
    source.parse(time).getTime
  }

  def main(args: Array[String]): Unit = {
    print(parse("2017-05-11 11:20:54"))
  }
}

