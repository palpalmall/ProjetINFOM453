package actors

import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.Signal
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import scala.util.{Try, Success, Failure}
/* import akka.actor.Actor

//a toy actor to test our scalatra servlet
class Mactor extends Actor {
  def receive: Receive = {
    case "One" => sender() ! List("two")
  }
}

class Transaction extends Actor {
  def receive: Receive = {
    case _ => Behaviors.same
  }
} */

object TransactionT {
  def apply(id: String): Behavior[TransactionMessage] = Behaviors.setup { context =>
    

    Behaviors.receiveMessage {
      case Tell(msg) =>
        val tellmsg = Try {
          
        }
        Behaviors.same
      }
      
    }
}