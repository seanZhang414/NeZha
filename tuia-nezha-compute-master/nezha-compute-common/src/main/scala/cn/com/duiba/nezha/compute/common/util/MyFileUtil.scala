package cn.com.duiba.nezha.compute.common.util

import java.nio.file.Paths

/**
 * Created by pc on 2016/11/23.
 */
object MyFileUtil {

  def getFilePath(saveType:String,pathPrefix:String,pathDomain:String): String ={
    val path = Paths.get(pathPrefix,pathDomain)
    saveType+path.toString
  }

  def getAppFilePath(saveType:String,pathPrefix:String,pathDomain:String,app:Int): String ={
    val path = Paths.get(pathPrefix,pathDomain,app.toString())
    saveType+path.toString
  }
}
