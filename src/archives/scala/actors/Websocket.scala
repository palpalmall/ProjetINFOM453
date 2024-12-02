package actors

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.ExecutionContextExecutor

object WebSocketServer {
  implicit val system: ActorSystem = ActorSystem("websocket-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def main(args: Array[String]): Unit = {
    val route = path("ws") {
      handleWebSocketMessages(webSocketFlow)
    }

    Http().bindAndHandle(route, "localhost", 8080)
    println("Serveur en ligne à http://localhost:8080/ws")
  }

  def webSocketFlow: Flow[Message, Message, _] = {
    val incoming: Sink[Message, _] = Sink.foreach[Message] {
      case TextMessage.Strict(text) => println(s"Reçu : $text")
      case _ => // Gérer d'autres types de messages si nécessaire
    }

    val outgoing: Source[Message, _] = Source.tick(
      initialDelay = scala.concurrent.duration.Duration.Zero,
      interval = scala.concurrent.duration.Duration.create(1, "second"),
      tick = TextMessage("Ping")
    )

    Flow.fromSinkAndSource(incoming, outgoing)
  }
}