package com.scala.akka.worker

import java.util.UUID

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import akka.actor.Actor.Receive
import com.scala.akka.domain.{Messages, WorkInfo}
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

/**
  * Created by zhang on 2016/4/9.
  */
class Worker extends  Actor{

  val MASTER_ADDRESS ="akka.tcp://MyMasterServer@192.168.1.104:8899/user/Master"
  var master: ActorSelection=null
  val id: String =UUID.randomUUID().toString

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit ={
    master = context.actorSelection(MASTER_ADDRESS)
    //id,内存，cpu核数
    master  ! new Messages.RegisterWorker(id,2048,8)
  }

  override def receive: Receive = {

    case Messages.RegistSuccess(id) => {
      //接受Master反馈的消息
      import context.dispatcher
      context.system.scheduler.schedule(0 millis, 5000 millis,self,Messages.SendHeartBeat)
    }
      //接受自身心跳信息
    case Messages.SendHeartBeat =>{
      master ! Messages.HeartBeat(id)
    }
  }
}
object Worker{
  def main(args: Array[String]) {
    val host ="192.168.1.104"
    val port ="9999"
    val confstr=s"""
                   |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
                   |akka.remote.netty.tcp.hostname = "$host"
                   |akka.remote.netty.tcp.port = "$port"
       """.stripMargin
    val config: Config = ConfigFactory.parseString(confstr)
    val actorSystem = ActorSystem("MyWorker",config)
    actorSystem.actorOf(Props[Worker],"Worker")
    actorSystem.awaitTermination()
  }
}
