package actors

import org.scalatra.*
import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.actor.typed.ActorRef as TAR 
import akka.actor.typed.ActorSystem as TAS 
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
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.SpawnProtocol
import scala.annotation.meta.param


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

  get("/user/:id/:name"){
    "user"
  }

}

class PopServerTyped(system: TAS[TeamManagerCommand]) extends ScalatraServlet with JacksonJsonSupport with FutureSupport {
  protected implicit def executor: ExecutionContext = system.executionContext
  protected implicit val jsonFormats: Formats = DefaultFormats

   before() {
    contentType = formats("json")
  }

  get("/") {
    //timeout required for the use of future
    implicit val timeout: Timeout = 5.seconds
    //Getting a val from myActor when asking with "One"
    Map("team" -> "Mael")
    //Sengind the val future to the client that sent the get to root
  }

  get("/createTeam/:id/:member") {
    implicit val scheduler: akka.actor.typed.Scheduler = system.scheduler
    //timeout required for the use of future
    implicit val timeout: Timeout = 5.seconds
    system.ask(replyTo =>
      CreateTeam("team1", List("Dzen1", "Mael1"), replyTo))
  }

  get("/tl/:list") {
    multiParams("id")
  }

  get("/future/:id/:member"){
    implicit val scheduler: akka.actor.typed.Scheduler = system.scheduler
    //timeout required for the use of future
    implicit val timeout: Timeout = 5.seconds
    val result: Future[Response] = system.ask(ref => CreateTeam("team122", List("Dzen122", "Mael122"), ref))
    result.map {
      case SuccessResponse(message) => Ok(Map("message" -> message))
      case FailureResponse(error)   => BadRequest(Map("error" -> error))
      case SuccessResponseTest(map) => Ok(Map("caca"->"pipi"))
    }
      
  }

  

}

