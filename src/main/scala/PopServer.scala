package com.example.app

import org.scalatra.*
import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.pattern.{ask, pipe}
import akka.util.LineNumbers.Result
import akka.util.Timeout
import scala.concurrent.duration.DurationInt
import scala.concurrent.Future
// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}
// JSON handling support from Scalatra
import org.scalatra.json._
import scala.util.Success
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure
import dispatch._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

//Scalatra servlet that has a behavior for each path in URL.
class PopServer(system: ActorSystem, myActor: ActorRef) extends ScalatraServlet with JacksonJsonSupport with FutureSupport {
  protected implicit def executor: ExecutionContext = system.dispatcher
  // Sets up automatic case class to JSON output serialization, required by
  // the JValueResult trait.
  protected implicit val jsonFormats: Formats = DefaultFormats
  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }
  //Command get to root
  get("/") {
    //timeout required for the use of future
    implicit val timeout: Timeout = 5.seconds
    //Getting a val from myActor when asking with "One"
    myActor ? "One"
    //Sengind the val future to the client that sent the get to root
  }

  get("/user"){
    "user"
  }

}


