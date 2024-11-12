package actors

import java.sql.{Connection, DriverManager, SQLException}


object DatabaseConnectivity extends App {
  
  //JDBC url for connectivity
  val jdbcUrl = "jdbc:mysql://127.0.0.1:3306/pops"
  val uname = "root"//MySQL username
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
}

object Server {
    sealed trait ServerCommand
    final case class getSmtg()
}