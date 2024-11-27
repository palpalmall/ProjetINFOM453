package com.example.app

import org.scalatra.*
import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.pattern.{ask, pipe}
import akka.util.LineNumbers.Result
import akka.util.Timeout
import scala.concurrent.duration.DurationInt

import scala.concurrent.Future

class PopServer(system: ActorSystem, myActor: ActorRef) extends ScalatraServlet {

  get("/") {
    implicit val timeout: Timeout = 5.seconds
    val future = myActor ? "One"
    future
  }

}
