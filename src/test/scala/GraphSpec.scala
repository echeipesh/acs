/**
 * User: eugene
 * Date: 12/9/13
 */

import akka.actor._
import akka.testkit.TestProbe
import org.scalatest._
import scala.concurrent.duration._
import Graph._


class GraphSpec extends FlatSpec with Matchers {
  implicit val system = ActorSystem("TestSys")
  val G_dist:Array[Array[Double]] = Array(
    Array( 0.0, 10.0, 20.0),
    Array(10.0,  0.0,  5.0),
    Array(20.0,  5.0,  0.0)
  )
  val G = system.actorOf( Props(classOf[Graph], G_dist) )
  val probe = TestProbe()

  "Graph" should "respond to Look(at)" in {
    probe.send(G, Look(0))
    probe.expectMsgPF(1.seconds){
      case x:Array[Edge] => x.toSet //don't care about the order
    } should equal ( Set(Edge(1,10,0), Edge(2,20,0)) )

    probe.send(G, Look(1))
    probe.expectMsgPF(1.seconds){
      case x:Array[Edge] => x.toSet //don't care about the order
    } should equal ( Set(Edge(0,10,0), Edge(2,5,0)) )
  }

}