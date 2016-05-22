package com.scala.akka.master

import akka.actor.{Actor, ActorSystem, Props}
import com.scala.akka.domain.{Messages, WorkInfo}
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.mutable
import scala.concurrent.duration._
/**
  * Created by zhang on 2016/4/9.
  */
class Master extends  Actor{

  val workers =new mutable.HashSet[WorkInfo]()
  val workerToIds =new mutable.HashMap[String,WorkInfo]()
  val timeOut=5000;

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit ={
    import context.dispatcher
   context.system.scheduler.schedule(0 millis, 5000 millis,self,Messages.TimeOutHeartBeat)
  }

  override def receive: Receive = {
    //接收Worker注册
    case Messages.RegisterWorker(id,memory,cores) =>{
        if (!workerToIds.contains(id)) {
          val workInfo: WorkInfo = new WorkInfo(id, memory, cores)
          workerToIds(id) = workInfo
          workers += workInfo
        }
      //返回成功的消息给Worker
      sender ! Messages.RegistSuccess(id)
    }

    case Messages.HeartBeat(id) =>{
      val dey: Long = System.currentTimeMillis()
      val workInfo: WorkInfo = workerToIds(id)
      workInfo.heartbeatTime=dey
    }

    case Messages.TimeOutHeartBeat =>{
     val curTime= System.currentTimeMillis()
      val filters: mutable.HashSet[WorkInfo] = workers.filter(curTime-_.heartbeatTime>5000)
      //将心跳间隔大于5秒的移除
      for(work <- filters){
        workers-=work
        workerToIds-=work.id
      }
        println("list--》size"+workers.size)
    }
  }
}


object  Master{

  def main(args: Array[String]) {
    val host ="192.168.1.104" //2016-16：01删除注释
    val port ="8899"
    val confstr=s"""
     |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
     |akka.remote.netty.tcp.hostname = "$host"
     |akka.remote.netty.tcp.port = "$port"
       """.stripMargin
    val config: Config = ConfigFactory.parseString(confstr)
    val actorSystem = ActorSystem("MyMasterServer",config)
    actorSystem.actorOf(Props[Master],"Master")
    actorSystem.awaitTermination()

  }
}
