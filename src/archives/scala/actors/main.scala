package actors
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable.Map

import akka.actor.typed.ActorSystem

import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Success, Failure}
import actors.*
object Main extends App {
  import akka.actor.typed.scaladsl.AskPattern.Askable
  import akka.util.Timeout
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  
  implicit val timeout: Timeout = Timeout(2.seconds)
  // Création du système d'acteurs
  val system: ActorSystem[TeamManagerCommand] = ActorSystem(TeamManagerActor(), "TeamManagerSystem")

  // Ajoutez le scheduler
  implicit val scheduler: akka.actor.typed.Scheduler = system.scheduler
  //j'utilise enfin des types futures ici donc je pense suis complet pour thibaut
  // 1. Création de l'équipe avec Dzen et Mael
  val createTeamFuture = system.ask(replyTo =>
    CreateTeam("team1", List("Dzen", "Mael"), replyTo)
  )

  createTeamFuture.onComplete {
    case Success(SuccessResponse(message)) =>
      println(s"Team Creation Success: $message")
    case Success(FailureResponse(error)) =>
      println(s"Team Creation Failure: $error")
    case Failure(exception) =>
      println(s"Unexpected Team Creation Error: ${exception.getMessage}")}

  // 2. Ajout de Luis à l'équipe
  val addMemberFuture = system.ask(replyTo =>
        AddTeamMember("team1", "Luis", replyTo)
      )

  addMemberFuture.onComplete {
        case Success(SuccessResponse(message)) =>
          println(s"Add Member Success: $message")
        case Success(FailureResponse(error)) =>
          println(s"Add Member Failure: $error")
        case Failure(exception) =>
          println(s"Unexpected Add Member Error: ${exception.getMessage}")}

  // 3. Obtenir le statut de Dzen
  val getStatusFuture = system.ask(replyTo =>
  GetTeamMemberStatus("team1", "Dzen", replyTo))

  getStatusFuture.onComplete {
    case Success(StatusResponse(name, Some(currentStatus))) =>
      println(s"Statut de $name : $currentStatus") 
    case Success(StatusResponse(name, None)) =>
      println(s"Statut de $name non défini")
    case Success(FailureResponse(error)) =>
      println(s"Get Status Failure: $error")
    case Failure(exception) =>
      println(s"Unexpected Get Status Error: ${exception.getMessage}")
  }


  val updateStatusFuture = system.ask(replyTo =>
  UpdateTeamMemberStatus("team1", "Dzen", "Disponible", replyTo)
)

  updateStatusFuture.onComplete {
    case Success(StatusResponse(name, Some(updatedStatus))) =>
      println(s"Statut mis à jour pour $name : $updatedStatus")
      
      // on peut envoyer serveur avec uen fonction comme par exemple sendToServer(name, updatedStatus)

    case Success(StatusResponse(name, None)) =>
      println(s"Échec de la mise à jour du statut pour $name : statut non défini")

    case Success(FailureResponse(error)) =>
      println(s"Update Status Failure: $error")

    case Failure(exception) =>
      println(s"Unexpected Update Status Error: ${exception.getMessage}")
  }


  // 5. Faire que Mael pinge Luis
  val pingFuture = system.ask(replyTo =>
  PingTeamMember("team1", "Mael", "Luis", replyTo)
)

  pingFuture.onComplete {
    case Success(PingResponse(from, to)) =>
      println(s"$from a pingé $to") 
    case Success(FailureResponse(error)) =>
      println(s"Ping Failure: $error")
    case Failure(exception) =>
      println(s"Unexpected Ping Error: ${exception.getMessage}")
}

  
  val getMoodFuture = system.ask(replyTo =>
    GetTeamMemberMood("team1", "Dzen", replyTo))

  getMoodFuture.onComplete {
    case Success(MoodResponse(name, Some(currentMood))) =>
      println(s"Humeur de $name : $currentMood") 
    case Success(MoodResponse(name, None)) =>
      println(s"Humeur de $name non définie")
    case Success(FailureResponse(error)) =>
      println(s"Get Mood Failure: $error")
    case Failure(exception) =>
      println(s"Unexpected Get Mood Error: ${exception.getMessage}")
  }

  val updateMoodFuture = system.ask(replyTo =>
  UpdateTeamMemberMood("team1", "Dzen", "Happy", replyTo)
)

  updateMoodFuture.onComplete {
    case Success(MoodResponse(name, Some(updatedMood))) =>
      println(s"Humeur mise à jour pour $name : $updatedMood") 
    case Success(FailureResponse(error)) =>
      println(s"Update Mood Failure: $error")
    case Failure(exception) =>
      println(s"Unexpected Update Mood Error: ${exception.getMessage}")
  }



                
              }

          
