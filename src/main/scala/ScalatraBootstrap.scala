import com.example.app.*
import org.scalatra.*
import jakarta.servlet.ServletContext
import _root_.akka.actor.{ActorRef, ActorSystem, Props}

class ScalatraBootstrap extends LifeCycle {

  val system = ActorSystem()
  val myActor: ActorRef = system.actorOf(Props[Mactor](), "myActor")


  override def init(context: ServletContext): Unit = {
    context.mount(new PopServer(system, myActor), "/*")
  }
}
