import akka.actor._
import scala.concurrent.duration._

class Main extends Actor {
  context.setReceiveTimeout(10.second)

  val colony = context.actorOf(Colony.Props(TspData.readTspFile("tsp.dat"), Params.default), "Colony")

  colony ! Colony.Start(10)

  def receive = {
    case Graph.Tour(length, path) =>
      println(s"TOUR($length): $path")
      colony ! Colony.Start(10)

    case ReceiveTimeout =>
      println("STOP STOP SOP!")
      context.system.shutdown()
  }
}

object Main extends App {
  println("Starting actors...")
  val system = ActorSystem("ACS")
  val myActor = system.actorOf(Props[Main], name = "MainActor")
}