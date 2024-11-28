/* name := "akka-scala-seed"

version := "1.0"

scalaVersion := s"3.3.4"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

lazy val akkaVersion = "2.10.0"

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.13",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  "com.typesafe.slick" %% "slick" % "3.5.0-RC1",
  "org.slf4j" % "slf4j-nop" % "2.0.0",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.5.0-RC1",
  "com.typesafe" % "config" % "1.4.2",
  "mysql" % "mysql-connector-java" % "8.0.33",
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.6.3",
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "org.scalatra" %% "scalatra-javax" % "3.1.+"
)
 */

val ScalatraVersion = "3.1.0"

ThisBuild / scalaVersion := "3.3.4"
ThisBuild / organization := "unamur"
resolvers += "Akka library repository".at("https://repo.akka.io/maven")
lazy val akkaVersion = "2.10.0"

lazy val hello = (project in file("."))
  .settings(
    name := "pops",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      "org.scalatra" %% "scalatra-jakarta" % ScalatraVersion,
      "org.scalatra" %% "scalatra-scalatest-jakarta" % ScalatraVersion % "test",
      "ch.qos.logback" % "logback-classic" % "1.5.6" % "runtime",
      "org.eclipse.jetty.ee10" % "jetty-ee10-webapp" % "12.0.10" % "container",
      "jakarta.servlet" % "jakarta.servlet-api" % "6.0.0" % "provided",
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.13",
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.15" % Test,
      "com.typesafe.slick" %% "slick" % "3.5.0-RC1",
      "org.slf4j" % "slf4j-nop" % "2.0.0",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.5.0-RC1",
      "com.typesafe" % "config" % "1.4.2",
      "org.scalatra" %% "scalatra-json" % "3.0.0-M5-jakarta",
      "org.json4s"   %% "json4s-jackson" % "4.1.0-M8",
      "org.dispatchhttp"        %% "dispatch-core"   % "2.0.0",
    ),
  )

enablePlugins(SbtTwirl)
enablePlugins(JettyPlugin)

Test / fork := true

Jetty / containerLibs := Seq("org.eclipse.jetty.ee10" % "jetty-ee10-runner" % "12.0.10" intransitive())
Jetty / containerMain := "org.eclipse.jetty.ee10.runner.Runner"
