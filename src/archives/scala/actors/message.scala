package actors

import akka.actor.typed.ActorRef

// ### CASE CLASSES ET TRAITS ###
sealed trait TeamManagerCommand
case class CreateTeam(id: String, members: List[String], replyTo: ActorRef[Response]) extends TeamManagerCommand
case class GetTeam(id: String, replyTo: ActorRef[Response]) extends TeamManagerCommand
case class AddTeamMember(teamId: String, member: String, replyTo: ActorRef[Response]) extends TeamManagerCommand
case class PingTeamMember(teamId: String, from: String, to: String, replyTo: ActorRef[Response]) extends TeamManagerCommand
case class UpdateTeamMemberStatus(teamId: String, member: String, status: String, replyTo: ActorRef[Response]) extends TeamManagerCommand
case class GetTeamMemberStatus(teamId: String, member: String, replyTo: ActorRef[Response]) extends TeamManagerCommand
case class UpdateTeamMemberMood(teamId: String, member: String, mood: String, replyTo: ActorRef[Response]) extends TeamManagerCommand
case class GetTeamMemberMood(teamId: String, member: String, replyTo: ActorRef[Response]) extends TeamManagerCommand

sealed trait TeamCommand
case class AddMember(member: String, replyTo: ActorRef[Response]) extends TeamCommand
case class UpdateMemberStatus(member: String, status: String, replyTo: ActorRef[Response]) extends TeamCommand
case class GetMemberStatus(member: String, replyTo: ActorRef[Response]) extends TeamCommand
case class UpdateMemberMood(member: String, mood: String, replyTo: ActorRef[Response]) extends TeamCommand
case class GetMemberMood(member: String, replyTo: ActorRef[Response]) extends TeamCommand
case class PingMember(member: String, by: String, replyTo: ActorRef[Response]) extends TeamCommand

sealed trait UserCommand
case class UpdateStatus(status: String, replyTo: ActorRef[Response]) extends UserCommand
case class GetStatus(replyTo: ActorRef[Response]) extends UserCommand 
case class UpdateMood(mood: String, replyTo: ActorRef[Response]) extends UserCommand
case class GetMood(replyTo: ActorRef[Response]) extends UserCommand
case class Ping(by: String, replyTo: ActorRef[Response]) extends UserCommand

sealed trait Response
case class SuccessResponse(message: String) extends Response
case class FailureResponse(error: String) extends Response
case class TeamInfo(id: String, members: Map[String, ActorRef[UserCommand]]) extends Response
//réponse pour encapsuler des données spécifiques pour envoyer aux serveurs puis raspberey par exemple
case class StatusResponse(name: String, status: Option[String]) extends Response
case class MoodResponse(name: String, mood: Option[String]) extends Response
case class PingResponse(from: String, to: String) extends Response
