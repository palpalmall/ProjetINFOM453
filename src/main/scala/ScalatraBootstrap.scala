import actors.*

import org.scalatra.*
import jakarta.servlet.ServletContext
import _root_.akka.actor.{ActorRef, ActorSystem, Props}
import akka.actor.typed.ActorSystem as TAS
import akka.actor.typed.SpawnProtocol


//initializer for scalatra servlet
class ScalatraBootstrap extends LifeCycle {
    // here is the init of our first actor. Might need to init all if we can't dynamically create child. We should be able to but require a good mapping.
    //val system = ActorSystem()
    //val myActor: ActorRef = system.actorOf(Props[Mactor](), "myActor")
    val system :TAS[TeamManagerCommand] = TAS(TeamManagerActor(), "TeamManagerActor")

    override def init(context: ServletContext): Unit = {
        //context.mount(new PopServer(system, myActor), "/*")
        //context.setInitParameter(org.scalatra.EnvironmentKey, "production")
        //context.setInitParameter(ScalatraBase.HostNameKey, "popsapp")
        context.mount(new PopServerTyped(system), "/*")
        context.setInitParameter(org.scalatra.EnvironmentKey, "production")
        context.setInitParameter(ScalatraBase.HostNameKey, "popsapp")
    }
}
