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
import org.json4s.MonadicJValue.jvalueToMonadic
import org.json4s.jvalue2extractable
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

  get("/createTeam/:id") {
    val teamId = params("id")
    implicit val scheduler: akka.actor.typed.Scheduler = system.scheduler
    //timeout required for the use of future
    implicit val timeout: Timeout = 5.seconds
    system.ask(replyTo =>
      CreateTeam(teamId, List("Dzen1", "Mael1"), replyTo))
  }
  // Route pour créer une équipe si c'est mael qui demande la creation
  post("/teams") {
    implicit val scheduler = system.scheduler
    implicit val timeout: Timeout = 5.seconds
    val teamId = (parsedBody \ "id").extractOpt[String].getOrElse(halt(400, "Team ID is required"))
    val members = (parsedBody \ "members").extractOpt[List[String]].getOrElse(List.empty)

    val result: Future[Response] = system.ask(replyTo => CreateTeam(teamId, members, replyTo))
    result.map {
      case SuccessResponse(message) => Ok(Map("message" -> message))
      case FailureResponse(error)   => BadRequest(Map("error" -> error))
    }
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
  // c'est si il y'a id et member dans l'url on peut faire autrement si c'est juste un objet json 
  get("/teams/:id/members/:member/pingers") {
  val teamId = params("id")
  val member = params("member")
  implicit val scheduler: akka.actor.typed.Scheduler = system.scheduler
  implicit val timeout: Timeout = 5.seconds
  val result: Future[Response] = system.ask(ref => GetPingersTeamMember(teamId, member, ref))

  result.map {
    case PingersResponse(pingers) =>
      Ok(Map("pingers" -> pingers))
    case FailureResponse(error) =>
      BadRequest(Map("error" -> error))
  }
}
 // Route pour récupérer les informations d'une équipe jsp si c'est necessaire pour nous
  get("/teams/:id") {
    implicit val scheduler = system.scheduler
    implicit val timeout: Timeout = 5.seconds
    val teamId = params("id")

    val result: Future[Response] = system.ask(replyTo => GetTeam(teamId, replyTo))
    result.map {
      case TeamInfo(id, members) => Ok(Map("id" -> id, "members" -> members.keys.toList))
      case FailureResponse(error) => NotFound(Map("error" -> error))
    }
  }

  // Route pour ajouter un membre à une équipe c'est si il y'a id  dans l'url on peut faire si c'est juste le json 
  post("/teams/:id/members") {
    implicit val timeout: Timeout = 5.seconds
    implicit val scheduler = system.scheduler
    val teamId = params("id")
    val member = (parsedBody \ "member").extractOpt[String].getOrElse(halt(400, "pas de membre"))

    val result: Future[Response] = system.ask(replyTo => AddTeamMember(teamId, member, replyTo))
    result.map {
      case SuccessResponse(message) => Ok(Map("message" -> message))
      case FailureResponse(error)   => BadRequest(Map("error" -> error))
    }
    
  }

  // Route pour mettre à jour le statut d'un membre ca c'est si il y'a id et member dans l'url on peut faire si c'est juste le json 
  post("/teams/:id/members/:member/status") {
    implicit val scheduler = system.scheduler
    implicit val timeout: Timeout = 5.seconds
    val teamId = params("id")
    val member = params("member")
    val status = (parsedBody \ "status").extractOpt[String].getOrElse(halt(400, "il faut le status"))

    val result: Future[Response] = system.ask(replyTo => UpdateTeamMemberStatus(teamId, member, status, replyTo))
    result.map {
      case SuccessResponse(message) => Ok(Map("message" -> message))
      case FailureResponse(error)   => BadRequest(Map("error" -> error))
    }
  }

  // Route pour récupérer le statut d'un membre c'est si il y'a id et member dans l'url on peut faire autrement  si c'est juste le json 
  get("/teams/:id/members/:member/status") {
    implicit val scheduler = system.scheduler
    implicit val timeout: Timeout = 5.seconds
    val teamId = params("id")
    val member = params("member")

    val result: Future[Response] = system.ask(replyTo => GetTeamMemberStatus(teamId, member, replyTo))
    result.map {
      case StatusResponse(name, status) => Ok(Map("name" -> name, "statut" -> status.getOrElse("N/A")))
      case FailureResponse(error)       => NotFound(Map("error" -> error))
    }
  }

  // Route pour mettre à jour l'humeur d'un membre c'est si il y'a id et member dans l'url on peut faire autrement si c'est juste le json 
  post("/teams/:id/members/:member/mood") {
    implicit val scheduler = system.scheduler
    implicit val timeout: Timeout = 5.seconds
    val teamId = params("id")
    val member = params("member")
    val mood = (parsedBody \ "mood").extractOpt[String].getOrElse(halt(400, "il faut le mood"))

    val result: Future[Response] = system.ask(replyTo => UpdateTeamMemberMood(teamId, member, mood, replyTo))
    result.map {
      case SuccessResponse(message) => Ok(Map("message" -> message))
      case FailureResponse(error)   => BadRequest(Map("error" -> error))
    }
    
  }

  // Route pour récupérer l'humeur d'un membr
  get("/teams/:id/members/:member/mood") {
    implicit val scheduler = system.scheduler
    implicit val timeout: Timeout = 5.seconds
    val teamId = params("id")
    val member = params("member")

    val result: Future[Response] = system.ask(replyTo => GetTeamMemberMood(teamId, member, replyTo))
    result.map {
      case MoodResponse(name, mood) => Ok(Map("name" -> name, "mood" -> mood.getOrElse("N/A")))
      case FailureResponse(error)   => NotFound(Map("error" -> error))
    }
  }

  // Route pour envoyer un ping d'un membre à un autre
  post("/teams/:id/ping") {
    implicit val scheduler = system.scheduler
    implicit val timeout: Timeout = 5.seconds
    val teamId = params("id")
    val from = (parsedBody \ "from").extractOpt[String].getOrElse(halt(400, "qui envoit ca ?"))
    val to = (parsedBody \ "to").extractOpt[String].getOrElse(halt(400, "qui recoit ?"))

    val result: Future[Response] = system.ask(replyTo => PingTeamMember(teamId, from, to, replyTo))
    result.map {
      case PingResponse(sender, rec) => Ok(Map("message" -> s"Ping  de $sender to $rec"))
      case FailureResponse(error)         => BadRequest(Map("error" -> error))
    }
  }

  // Route pour récupérer les pingers d'un membre
  get("/teams/:id/members/:member/pingers") {
    implicit val scheduler = system.scheduler
    implicit val timeout: Timeout = 5.seconds
    val teamId = params("id")
    val member = params("member")

    val result: Future[Response] = system.ask(replyTo => GetPingersTeamMember(teamId, member, replyTo))
    result.map {
      case PingersResponse(pingers) => Ok(Map("pingers" -> pingers))
      case FailureResponse(error)   => BadRequest(Map("error" -> error))
    }
  }

  

}

