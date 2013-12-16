import akka.actor._
import scala.concurrent.duration._

class Main extends Actor {
  import context.dispatcher
  context.system.scheduler.scheduleOnce(30 seconds, self, "STOP")

  val colony = context.actorOf(Colony.Props(TspData.readTspFile("tsp.dat"), Params.default), "Colony")

  colony ! Colony.Start(10)

  def receive = {
    case Graph.Tour(length, path) =>
      println(s"TOUR($length)")
      colony ! Colony.Start(10)

    case "STOP" =>
      context.system.shutdown()
  }
}

object Main extends App {
  println("Starting actors...")
  val system = ActorSystem("ACS")
  val myActor = system.actorOf(Props[Main], name = "MainActor")
}