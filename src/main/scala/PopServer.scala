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
     implicit val scheduler: akka.actor.typed.Scheduler = system.scheduler
    //Getting a val from myActor when asking with "One"
    val TProf: Future[Response] = system.ask(ref => CreateTeam("Prof", List("Dumas", "Schumacher","Jacquet","Vanhoof","Hallaert"), ref))
    TProf.map {
      case SuccessResponse(message) => Ok(Map("message" -> message))
      case FailureResponse(error)   => BadRequest(Map("error" -> error))
    }
    val tEleve: Future[Response] = system.ask(ref => CreateTeam("Eleve", List("Mael", "Luis","Rosny","Noe","Dzenetan"), ref))
    tEleve.map {
      case SuccessResponse(message) => Ok(Map("message" -> message))
      case FailureResponse(error)   => BadRequest(Map("error" -> error))
    }

    
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
    }
      
  }
  // ================= PING ======================
  // c'est si il y'a id et member dans l'url on peut faire autrement si c'est juste un objet json 
  get("/ping/:team_id/:member_id") {
  val team_id = params("team_id")
  val member_id = params("member_id")
  
  println("get ping"+ team_id+ member_id)
  
  implicit val scheduler: akka.actor.typed.Scheduler = system.scheduler
  implicit val timeout: Timeout = 5.seconds
  val result: Future[Response] = system.ask(ref => GetPingersTeamMember(team_id, member_id, ref))

  result.map {
    case PingersResponse(pingers) =>
      Ok(pingers)
    case FailureResponse(error) =>
      BadRequest(Map("error" -> error))
    }
  }
  
  // Route pour envoyer un ping d'un membre à un autre
  post("/ping/:team_id/:from/:to") {
    println(parsedBody)
    implicit val scheduler = system.scheduler
    implicit val timeout: Timeout = 5.seconds
    val team_id = params("team_id")
    val from = params("from")
    val to = params("to")
    
    println("post ping"+ team_id+ from+ to)

    val result: Future[Response] = system.ask(replyTo => PingTeamMember(team_id, from, to, replyTo))
    result.map {
      case PingResponse(sender, rec) => Ok(Map("message" -> s"Ping  de $sender to $rec"))
      case FailureResponse(error)         => BadRequest(Map("error" -> error))
    }
  }

  // ======================= CREATION ============================
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

  // ===================== STATUS ===========================
  // Route pour récupérer le statut d'un membre c'est si il y'a id et member dans l'url on peut faire autrement  si c'est juste le json 
  get("/status/:team_id/:member_id") {
    implicit val scheduler = system.scheduler
    implicit val timeout: Timeout = 5.seconds
    val team_id = params("team_id")
    val member_id = params("member_id")

    println("get status"+ team_id+ member_id)

    val result: Future[Response] = system.ask(replyTo => GetTeamMemberStatus(team_id, member_id, replyTo))
    result.map {
      case StatusResponse(name, status) => Ok(Map("name" -> name, "statut" -> status.getOrElse("N/A")))
      case FailureResponse(error)       => NotFound(Map("error" -> error))
    }
  }
  
  // Route pour mettre à jour le statut d'un membre ca c'est si il y'a id et member dans l'url on peut faire si c'est juste le json 
  post("/status/:team_id/:member_id/:status") {
    implicit val scheduler = system.scheduler
    implicit val timeout: Timeout = 5.seconds
    val team_id = params("team_id")
    val member_id = params("member_id")
    val status = params("status")

    println("post status"+ team_id+ member_id+ status)

    val result: Future[Response] = system.ask(replyTo => UpdateTeamMemberStatus(team_id, member_id, status, replyTo))
    result.map {
      case SuccessResponse(message) => Ok(Map("message" -> message))
      case FailureResponse(error)   => BadRequest(Map("error" -> error))
      case StatusResponse(name, status) => Map(name -> status)
    }
  }

  // ======================= MOOD ============================
  // Route pour récupérer l'humeur d'un membr
  get("/mood/:team_id/:member_id") {
    implicit val scheduler = system.scheduler
    implicit val timeout: Timeout = 5.seconds
    val team_id = params("team_id")
    val member_id = params("member_id")

    println("get mood"+ team_id+ member_id)

    val result: Future[Response] = system.ask(replyTo => GetTeamMemberMood(team_id, member_id, replyTo))
    result.map {
      case MoodResponse(name, mood) => Ok(Map("name" -> name, "mood" -> mood.getOrElse("N/A")))
      case FailureResponse(error)   => NotFound(Map("error" -> error))
    }
  }
  
  // Route pour mettre à jour l'humeur d'un membre c'est si il y'a id et member dans l'url on peut faire autrement si c'est juste le json 
  post("/mood/:team_id/:member_id/:mood") {
    implicit val scheduler = system.scheduler
    implicit val timeout: Timeout = 5.seconds
    val team_id = params("team_id")
    val member_id = params("member_id")
    val mood = params("mood")

    println("post mood"+ team_id+ member_id+ mood)

    val result: Future[Response] = system.ask(replyTo => UpdateTeamMemberMood(team_id, member_id, mood, replyTo))
    result.map {
      case SuccessResponse(message) => Ok(Map("message" -> message))
      case FailureResponse(error)   => BadRequest(Map("error" -> error))
      case MoodResponse(name, Some(newMood)) => Map(name -> newMood)
    } 
  }

  // ================================== TEMPERATURE ==========================
  // post("/temperature"){}


}

