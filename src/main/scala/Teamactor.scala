package actors
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable.Map
import actors.*
import scala.util.{Try, Success, Failure}

object TeamActor {
  def apply(id: String): Behavior[TeamCommand] = Behaviors.setup { context =>
    var members: Map[String, ActorRef[UserCommand]] = Map()

    Behaviors.receiveMessage {
      case AddMember(member, replyTo) =>
        val addResult = Try {
          require(member.nonEmpty, "meme doit etre valide ")
          val userActor = context.spawn(UserActor(member), s"user-$member")
          members += (member -> userActor)
          println(member)
          s"Member $member ajouté avec l'equipe $id avec succès"
        }

        addResult match {
          case Success(message) => replyTo ! SuccessResponse(message)
          case Failure(exception) => replyTo ! FailureResponse(exception.getMessage)
        }
        Behaviors.same

      case UpdateMemberStatus(member, status, replyTo) =>
        members.get(member) match {
          case Some(userActor) =>
            userActor ! UpdateStatus(status, replyTo)
          case None =>
            replyTo ! FailureResponse(s"Member $member est pas dans equipe $id")
        }
        Behaviors.same

      case GetMemberStatus(member, replyTo) =>
        members.get(member) match {
          case Some(userActor) =>
            userActor ! GetStatus(replyTo) 
          case None =>
            replyTo ! FailureResponse(s"Member $member est pas dans equipe $id")
        }
        Behaviors.same

      case UpdateMemberMood(member, mood, replyTo) =>
        members.get(member) match {
          case Some(userActor) =>
            userActor ! UpdateMood(mood, replyTo) 
          case None =>
            replyTo ! FailureResponse(s"Member $member est pas dans equipe $id")
        }
        Behaviors.same

      case GetMemberMood(member, replyTo) =>
        members.get(member) match {
          case Some(userActor) =>
            userActor ! GetMood(replyTo)
          case None =>
            replyTo ! FailureResponse(s"Member $member est pas dans equipe $id")
        }
        Behaviors.same
     case GetPingersMember(member, replyTo) =>
          members.get(member) match {
            case Some(userActor) =>
              userActor ! GetPingers(replyTo)
            case None =>
              replyTo ! FailureResponse(s"Le membre $member n'existe pas dans l'équipe")
    }
        Behaviors.same

      case PingMember(member, by, replyTo) =>
        members.get(member) match {
          case Some(userActor) =>
            userActor ! Ping(by, replyTo)
          case None =>
            replyTo ! FailureResponse(s"Member $member est pas dans equipe $id")
        }
        Behaviors.same
    }
  }
}
