package actors

import java.sql.{Connection, DriverManager, SQLException}
import com.typesafe.config._
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.Signal
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem

//Slick modelling of database tables
class Team(tag: Tag) extends Table[(Int, String)](tag, "TEAM") {
  def teamid = column[Int]("TeamId", O.PrimaryKey)
  def teamname = column[String]("TeamName")
  def * = (teamid, teamname)
}

val teams = TableQuery[Team]

class Client(tag: Tag) extends Table[(Int, String, Int)](tag, "CLIENT") {
  def clientid = column[Int]("ClientId", O.PrimaryKey)
  def clientname = column[String]("ClientName")
  def teamkey = column[Int]("TeamKey")
  def * = (clientid, clientname, teamkey)
  def team = foreignKey("TeamFK", teamkey, teams)(_.teamid)
}

val clients = TableQuery[Client]

class User(tag: Tag) extends Table[(Int, String, Int, Int)](tag, "USER") {
  def userid = column[Int]("UserId", O.PrimaryKey)
  def username = column[String]("UserName")
  def clientkey = column[Int]("ClientKey")
  def userkey = column[Int]("UserKey")
  def * = (userid, username, clientkey, userkey)
  def team = foreignKey("ClientKey", clientkey, clients)
  def user = foreignKey("UserKey", userkey, clients)
}

val user = TableQuery[User]

//Test of database connection through Slick 
object DatabaseConnectivity extends App {
  /* //JDBC url for connectivity
  val jdbcUrl = "jdbc:mysql://192.168.1.37:3307/pops"
  val uname = "user0"//MySQL username
  val pwd = "Un@murBE"//MySQL password
  var connection: Connection = null
  
  // try block for connectivity and if error or 
  // exception occurs it will be catched by catch block
  try{
    
    //Load Driver Class
    Class.forName("com.mysql.cj.jdbc.Driver")
    
    // Establish connection using url,
    // mysql username an password
    connection = DriverManager.getConnection(jdbcUrl, uname, pwd)
    println("Connection successful!!")
  } catch{
    case e : ClassNotFoundException =>
      println("Error loading jdbc driver class!!")
      e.printStackTrace()
    case e: SQLException =>
      println("Error Connecting to the database!!")
      e.printStackTrace()
  }   */

  val db = Database.forConfig("mydb")
  
  val q = teams.map(_.teamname)
  val action = q.result
  val result: Future[Seq[String]] = db.run(action)
  result.onComplete {
    case scala.util.Success(t) => t.foreach(x => print(x + '\n'))
    case scala.util.Failure(s) => println(s"failure")
  }
  Thread.sleep(10000)
}

//Actor TeamMannager that handle a map of each team name as String -> Team Actor 
object TeamManager {
  sealed trait TeamManagerCommand
  final case class AddInTeam(deviceID: String, user: String, team: String, replyTo: ActorRef[Team]) extends TeamManagerCommand
  final case class TeamTerminated(actor: ActorRef[Team.TeamCommand] , name: String) extends TeamManagerCommand

  

  //Setting the Behavior as TeamManager spawn
  def apply(): Behavior[TeamManagerCommand] = 
    teams(scala.collection.mutable.Map.empty)

  //Defining the behaviors
  private def teams(teams: scala.collection.mutable.Map[String, ActorRef[Team.TeamCommand]]): Behavior[TeamManagerCommand] =
    Behaviors.receive { (context, message )=>
      message match {
        case AddInTeam(devID, user, team, replyTo) => 
          teams.get(team) match {
            //In case an actor with the team name exists, tell it to add the device replyTo in the right user
            case Some(teamActor) => 
              teamActor ! Team.AddDevice(devID, user, team, replyTo)
              Behaviors.same
            case None => 
              //In case there isn't a team with name, Creating a team without checking it exists in DB
              //TODO find a way to check if the team exists in DB
              context.log.info(s"Creating a team with name $team")
              val newTeam = context.spawn(Team(), team)
              context.watchWith(newTeam, TeamTerminated(newTeam, team)) //Whenever the newTeam actor ends, it sends a signal to this TeamManager
              teams += team -> newTeam
              Behaviors.same
          }
        case TeamTerminated(_, name) =>
            context.log.info("Termination of team : {}", name)
            teams.remove(name)
            Behaviors.same
        
      }  
    }

}

//Actor that map all user and devices that requires info about them
object Team {

  sealed trait TeamCommand
  final case class AddDevice(deviceID: String, user: String, team: String, replyTo: ActorRef[Team]) extends TeamCommand

  def apply(): Behavior[TeamCommand] =
    users(scala.collection.mutable.Map.empty)

  def users(users: scala.collection.mutable.Map[String, ActorRef[User]]): Behavior[TeamCommand] = 
    Behaviors.receive{ (context, message) =>
      message match {
      case _ => Behaviors.same
      }
    }
}

//Actor that represent a User from the database, might not be used
object User {

}


