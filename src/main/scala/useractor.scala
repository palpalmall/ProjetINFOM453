package actors
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable.Map
import actors.*
import akka.actor.typed.Behavior

object UserActor {
  def apply(name: String): Behavior[UserCommand] = Behaviors.setup { context =>
    var status: Option[String] = None
    var mood: Option[String] = None
    var pingers : List[String] = List.empty

    Behaviors.receiveMessage {
      case UpdateStatus(newStatus, replyTo) =>
        val result: Either[String, String] =
          if (newStatus.isEmpty) Left("Statut doit etre valid")
          else {
            status = Some(newStatus)
            Right(s"Statut de $name devient mtn $newStatus")
          }

        result match {
          case Right(updatedStatus) => 
            replyTo ! StatusResponse(name, Some(newStatus))
          case Left(errorMessage) =>
            replyTo ! FailureResponse(errorMessage)
        }
        Behaviors.same

      case GetStatus(replyTo) =>
        status match {
          case Some(s) => replyTo ! StatusResponse(name, status)
          case None    => replyTo ! FailureResponse(s"Status de $name  pas encore defini ")
        }
        Behaviors.same

      case UpdateMood(newMood, replyTo) =>
        val result: Either[String, String] =
          if (newMood.isEmpty) Left("Mood doit etre valide")
          else {
            mood = Some(newMood)
            Right(s"Mood de $name devient $newMood")
          }

        result match {
          case Right(updatedMood) =>
            replyTo ! MoodResponse(name, Some(newMood))
          case Left(errorMessage) =>
            replyTo ! FailureResponse(errorMessage)
        }
        Behaviors.same

      case GetMood(replyTo) =>
        mood match {
          case Some(m) => replyTo ! MoodResponse(name, mood)
          case None    => replyTo ! FailureResponse(s"Mood de $name pas encore set")
        }
        Behaviors.same
      case Ping(by, replyTo) =>
        replyTo ! PingResponse(by, name)
        pingers = pingers ::: List(by)
        println(pingers)
        Behaviors.same
      case GetPingers(replyTo) =>
        replyTo ! PingersResponse(pingers.toList)
        pingers = List.empty
        Behaviors.same
    }
  }
}


