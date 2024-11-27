package com.example.app

import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.Signal
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.Actor

//a toy actor to test our scalatra servlet
class Mactor extends Actor {
  def receive: Receive = {
    case "One" => sender() ! "two"
  }
}