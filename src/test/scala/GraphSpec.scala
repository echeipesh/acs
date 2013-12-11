/**
 * User: eugene
 * Date: 12/9/13
 */

import akka.actor._
import akka.testkit.TestProbe
import org.scalatest._
import scala.concurrent.duration._
import Graph._


class GraphSpec extends FlatSpec with Matchers with BeforeAndAfterAll {
  implicit val system = ActorSystem("TestSys")
  override def afterAll()  { system.shutdown() }

  val G_dist:Array[Array[Double]] = Array(
    Array( 0.0, 10.0, 20.0),
    Array(10.0,  0.0,  5.0),
    Array(20.0,  5.0,  0.0)
  )
  val G = system.actorOf( Graph.Props(G_dist), "GraphActor")
  val probe = TestProbe()
  //avoid order for tests
  val PFtoSet:PartialFunction[Any, Set[Edge]] = {case x:Array[Edge] => x.toSet}

  "Graph" should "respond to Look(at)" in {
    probe.send(G, Look(0))
    probe.expectMsgPF(1.seconds)(PFtoSet) should equal ( Set(Edge(1,10,0), Edge(2,20,0)) )

    probe.send(G, Look(1))
    probe.expectMsgPF(1.seconds)(PFtoSet) should equal ( Set(Edge(0,10,0), Edge(2,5,0)) )
  }

  it should "update on Travel(from, to)" in {
    probe.send(G, Travel(0,1))
    probe.send(G, Look(0))

    probe.expectMsgPF(1.seconds)(PFtoSet) should contain (Graph.localTrailUpdate(Edge(1,10,0)))
  }
}