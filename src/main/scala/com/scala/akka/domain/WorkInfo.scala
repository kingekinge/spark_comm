package com.scala.akka.domain

/**
  * Created by zhang on 2016/4/9.
  */
class WorkInfo(val id:String,val memory:Long,val cores:Int) extends Serializable{

  //记录最后一次心跳时间
  var heartbeatTime:Long=_;

}
