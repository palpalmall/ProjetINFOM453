package actors
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import scala.collection.immutable.Map
import actors.*
object TeamManagerActor {
  def apply(): Behavior[TeamManagerCommand] = Behaviors.setup { context =>
    var teams: Map[String, (ActorRef[TeamCommand], Option[List[String]])] = Map()

    Behaviors.receiveMessage {
      case CreateTeam(id, members, replyTo) =>
        val createResult: Either[String, String] =
          if (id.isEmpty) Left("Teamid doit etre valide ")
          else {
            val teamActor = context.spawn(TeamActor(id), s"team-$id")
            teams += (id -> (teamActor, Some(members)))
            Right(s"Team $id cree avec  succes")
          }

        createResult match {
          case Right(successMessage) =>
            members.foreach(member => teams(id)._1 ! AddMember(member, replyTo))
            replyTo ! SuccessResponse(successMessage)
          case Left(errorMessage) =>
            replyTo ! FailureResponse(errorMessage)
        }
        Behaviors.same

      case GetTeam(id, replyTo) =>
        teams.get(id) match {
          case Some((_, Some(members))) =>
            val memberActors = members.map(member => member -> context.spawn(UserActor(member), s"user-$member")).toMap
            replyTo ! TeamInfo(id, memberActors)
          case Some((_, None)) =>
            replyTo ! TeamInfo(id, Map.empty)
          case None =>
            replyTo ! FailureResponse(s" $id pas encore crée")
        }
        Behaviors.same

      case AddTeamMember(teamId, member, replyTo) =>
        teams.get(teamId) match {
          case Some((teamActor, Some(members))) if !members.contains(member) =>
            val updatedMembers = members :+ member
            teams += (teamId -> (teamActor, Some(updatedMembers)))
            teamActor ! AddMember(member, replyTo)
          case Some((_, Some(_))) =>
            replyTo ! FailureResponse(s"le membre $member est deja dans $teamId")
          case Some((_, None)) =>
            replyTo ! FailureResponse(s"l'equipe  $teamId est sans membr")
          case None =>
            replyTo ! FailureResponse(s" $teamId existe pas")
        }
        Behaviors.same

      case PingTeamMember(teamId, from, to, replyTo) =>
        teams.get(teamId) match {
          case Some((teamActor, Some(members))) if members.contains(from) && members.contains(to) =>
            teamActor ! PingMember(to, from, replyTo)
          case Some((_, Some(_))) =>
            replyTo ! FailureResponse(s"soit  $from ou $to n'est mbr $teamId")
          case Some((_, None)) =>
            replyTo ! FailureResponse(s"l'equipe $teamId a pas de membre")
          case None =>
            replyTo ! FailureResponse(s"l'equipe $teamId pas trouvée")
        }
        Behaviors.same
      case UpdateTeamMemberStatus(teamId, member, status, replyTo) =>
         teams.get(teamId) match {
          case Some((teamActor, Some(members))) if members.contains(member) =>
            teamActor ! UpdateMemberStatus(member, status, replyTo)
          case Some((_, Some(_))) =>
            replyTo ! FailureResponse(s" $member pas trouvé $teamId")
          case Some((_, None)) =>
            replyTo ! FailureResponse(s"l'equipe $teamId pas de membre")
          case None =>
            replyTo ! FailureResponse(s"l'equipe $teamId pas trouvé")
        }
        Behaviors.same
      case GetTeamMemberStatus(teamId, member, replyTo) =>
        teams.get(teamId) match {
          case Some((teamActor, Some(members))) if members.contains(member) =>
            teamActor ! GetMemberStatus(member, replyTo)
          case Some((_, Some(_))) =>
            replyTo ! FailureResponse(s" $member n'est pas dans l'equipe $teamId")
          case Some((_, None)) =>
            replyTo ! FailureResponse(s"l'equipe $teamId a pas de membre")
          case None =>
            replyTo ! FailureResponse(s"l'equipe $teamId not found")
        }
        Behaviors.same

      case UpdateTeamMemberMood(teamId, member, mood, replyTo) =>
        teams.get(teamId) match {
          case Some((teamActor, Some(members))) if members.contains(member) =>
            teamActor ! UpdateMemberMood(member, mood, replyTo)
          case Some((_, Some(_))) =>
            replyTo ! FailureResponse(s" $member n'est pas dans l'equipe $teamId")
          case Some((_, None)) =>
            replyTo ! FailureResponse(s"l'equipe $teamId a pas de membre")
          case None =>
            replyTo ! FailureResponse(s"l'equipe $teamId not found")
        }
        Behaviors.same

      case GetTeamMemberMood(teamId, member, replyTo) =>
        teams.get(teamId) match {
          case Some((teamActor, Some(members))) if members.contains(member) =>
            teamActor ! GetMemberMood(member, replyTo)
          case Some((_, Some(_))) =>
            replyTo ! FailureResponse(s" $member n'est pas dans l'equipe $teamId")
          case Some((_, None)) =>
            replyTo ! FailureResponse(s"l'equipe $teamId a pas de membre")
          case None =>
            replyTo ! FailureResponse(s"l'equipe $teamId not found")
        }
        Behaviors.same

    }
    }
  }

