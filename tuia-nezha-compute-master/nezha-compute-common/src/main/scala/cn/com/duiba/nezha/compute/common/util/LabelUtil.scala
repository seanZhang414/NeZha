package cn.com.duiba.nezha.compute.common.util

/**
 * Created by pc on 2016/12/16.
 */
object LabelUtil {
  def getLabel(label: String): Double = {
    if (label == null || label.size == 0) {
      0.0
    } else {
      if (label.toDouble >= 1.0) 1.0 else 0.0
    }
  }
}
