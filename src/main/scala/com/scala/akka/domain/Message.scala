package com.scala.akka.domain

/**
  * Created by zhang on 2016/4/9.
  */
trait Message extends Serializable

object  Messages extends Serializable{
  case class RegisterWorker(id : String ,memory: Long,cores: Int) extends Message

  case class RegistSuccess(id:String) extends Message

  case class HeartBeat(id:String) extends  Message
  case object SendHeartBeat extends  Message
  case object TimeOutHeartBeat extends  Message
}
