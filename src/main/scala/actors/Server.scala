package actors

import java.sql.{Connection, DriverManager, SQLException}

import com.typesafe.config._
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.Await

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }
import scala.concurrent._


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

object DatabaseConnectivity extends App {
  //JDBC url for connectivity
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
  }  

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


object Server {
  sealed trait ServerCommand
  final case class getSmtg()
}