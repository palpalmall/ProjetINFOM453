package actors

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorRef

import akka.actor.Actor._

import java.io.File

import akka.actor.{Props, Actor, ActorSystem}
import com.typesafe.config.ConfigFactory

object Status
//handle the database managment
object DB {
    case class GetUserStatus(user: String)
    case class UpdateUserStatus(user: String, status: String)
    case class AddUser(rasp: ActorRef, user: String)
}

class DB extends Actor {
    import DB._
    var RaspAndPop: List[(ActorRef, String)]= List()

    def receive = {
        case AddUser(rasp, user) => 
            RaspAndPop = (rasp,user)+:RaspAndPop
            RaspAndPop.map((x,y) => println(s"($x,$y)"))
        case GetUserStatus(user) => sender() ! Status
            
    }

}

//handle the raspberry side
object Raspberry {
   case class SendUpdate(user:String, status: String)
   case class SetDB(db: ActorRef)
}

class Raspberry extends Actor{
    import Raspberry._
    var db: ActorRef = null

    def receive = {
        //may require initiation at creation
        case SetDB(dbs: ActorRef) => db = dbs 
        //send any change to the database (i.e slap, etc)
        case SendUpdate(user, status) => 
            
        
        //receive a change of status of a pop from the database
        case Status => 
            println("status received")
    }
}


object Arduino {
    
}


class LocalActor extends Actor{
  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    /*
      Connect to remote actor. The following are the different parts of actor path

      akka.tcp : enabled-transports  of remote_application.conf

      RemoteSystem : name of the actor system used to create remote actor

      127.0.0.1:5150 : host and port

      user : The actor is user defined

      remote : name of the actor, passed as parameter to system.actorOf call

     */
    val remoteActor = context.actorSelection("akka.tcp://RemoteSystem@127.0.0.1:5150/user/remote")
    println("That 's remote:" + remoteActor)
    remoteActor ! "hi"
  }
  override def receive: Receive = {

    case msg:String => {
      println("got message from remote" + msg)
    }
  }
}

class RemoteActor extends Actor {
  override def receive: Receive = {
    case msg: String => {
      println("remote received " + msg + " from " + sender())
      sender() ! "hi"
    }
    case _ => println("Received unknown msg ")
  }
}


object ActorsTest extends App {
    /* import Raspberry._
    import DB._
    val system = ActorSystem("system")
    val db = system.actorOf(Props(DB()), "db")
    val rasp1 = system.actorOf(Props(Raspberry()), "rasp1")
    val rasp2 = system.actorOf(Props(Raspberry()), "rasp2")
    db ! AddUser(rasp1, "marc")
    println(db) */

    val configFile = getClass.getClassLoader.getResource("local_application.conf").getFile
    val config = ConfigFactory.parseFile(new File(configFile))
    val system = ActorSystem("ClientSystem",config)
    val localActor = system.actorOf(Props(LocalActor()), "local")
}


