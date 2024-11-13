package actors

import java.sql.{Connection, DriverManager, SQLException}

import com.typesafe.config._
import slick.jdbc.JdbcBackend.Database



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
  
}

object Server {
  sealed trait ServerCommand
  final case class getSmtg()
}