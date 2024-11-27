import com.example.app.*
import org.scalatra.*
import jakarta.servlet.ServletContext
import _root_.akka.actor.{ActorRef, ActorSystem, Props}


//initializer for scalatra servlet
class ScalatraBootstrap extends LifeCycle {
    // here is the init of our first actor. Might need to init all if we can't dynamically create child. We should be able to but require a good mapping.
    val system = ActorSystem()
    val myActor: ActorRef = system.actorOf(Props[Mactor](), "myActor")


    override def init(context: ServletContext): Unit = {
        context.mount(new PopServer(system, myActor), "/*")
    }
}
