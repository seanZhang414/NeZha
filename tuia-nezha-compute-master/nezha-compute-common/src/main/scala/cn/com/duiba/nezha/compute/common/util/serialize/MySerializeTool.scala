package cn.com.duiba.nezha.compute.common.util.serialize

import java.io._


/**
 * Created by pc on 2016/12/21.
 */
object MySerializeTool {
  def object2String(obj: AnyRef): String = {
    val baops = new ByteArrayOutputStream
    val oos = new ObjectOutputStream(baops)
    oos.writeObject(obj)
    val bytes= baops.toByteArray
    val objBody = new String(bytes,"ISO-8859-1")
    oos.close()
    objBody
  }

  @SuppressWarnings(Array("unchecked"))
  def getObjectFromString[T <: Serializable](objBody: String): T = {
    val bytes= objBody.getBytes("ISO-8859-1")
    val ois = new ObjectInputStream(new ByteArrayInputStream(bytes))
    val ret = ois.readObject.asInstanceOf[T]
    ois.close()
    ret
  }
}
