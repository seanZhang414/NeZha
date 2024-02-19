package cn.com.duiba.nezha.compute.common.util

/**
 * Created by pc on 2016/12/16.
 */
object MyStringUtil {

  def stringStd(src: String): String = {

    if (src == "") {
      null
    } else {
      src
    }
  }

  def stringListStd(src: List[String]): List[String] = {

    if (src != null) {
      src.map(stringStd)
    } else {
      src
    }
  }
}
