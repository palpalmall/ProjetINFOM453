package com.example.app

import org.scalatra.*
import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.pattern.{ask, pipe}
import akka.util.LineNumbers.Result
import akka.util.Timeout
import scala.concurrent.duration.DurationInt

import scala.concurrent.Future


//Scalatra servlet that has a behavior for each path in URL.
class PopServer(system: ActorSystem, myActor: ActorRef) extends ScalatraServlet {
  //Command get to root
  get("/") {
    //timeout required for the use of future
    implicit val timeout: Timeout = 5.seconds
    //Getting a val from myActor when asking with "One"
    val future = myActor ? "One"
    //Sengind the val future to the client that sent the get to root
    future
  }

}
