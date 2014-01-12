package acs.akka

import akka.actor._
import scala.concurrent.duration._
import acs.{Params, TspData}
import acs.Types._

class Main extends Actor {
  import context.dispatcher
  context.system.scheduler.scheduleOnce(30 seconds, self, "STOP")

  val g:Matrix[Double] = TspData.readTspFile("tsp.dat")
  val colony = context.actorOf(ColonyActor.Props(g, Params.forGraph(g)), "acs.akka.Colony")

  colony ! ColonyActor.Start(10)

  def receive = {
    case Tour(length, path) =>
      println(s"TOUR($length)")
      colony ! ColonyActor.Start(10)

    case "STOP" =>
      context.system.shutdown()
  }
}

object Main extends App {
  println("Starting actors...")
  val system = ActorSystem("ACS")
  val myActor = system.actorOf(Props[Main], name = "MainActor")
}