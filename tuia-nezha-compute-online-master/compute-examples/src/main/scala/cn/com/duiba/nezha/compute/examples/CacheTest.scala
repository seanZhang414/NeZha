package cn.com.duiba.nezha.compute.examples

import cn.com.duiba.nezha.compute.milib.ModelCache.ModelCache



object CacheTest {
  val cache = new ModelCache()
  def main(args: Array[String]): Unit = {

    cache.set("1", 0.9)
    val v1 = get("1")
    val v2=  get("2")
    println(v1)
    println( v2)

  }

  def get(key:String): Option[Double] ={
    val value = cache.get(key)
    if(value==null){
      None
    }else{
      Some(value)
    }
  }
}
