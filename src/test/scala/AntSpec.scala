/**
 * User: eugene
 * Date: 12/9/13
 */

import akka.actor._
import akka.testkit.TestProbe
import org.scalatest._


class AntSpec extends FlatSpec with Matchers with BeforeAndAfterAll {
  implicit val system = ActorSystem("TestSys")
  override def afterAll()  { system.shutdown() }

  val G_dist:Array[Array[Double]] = Array(
    Array( 0.0, 10.0, 20.0),
    Array(10.0,  0.0,  5.0),
    Array(20.0,  5.0,  0.0)
  )
  val G = system.actorOf( Graph.Props(G_dist), "GraphActor")

  "Ant" should "send Travel in response to view update" in {
    val probe = TestProbe()
    val ant =  system.actorOf( Ant.Props(probe.ref, 0))
    probe.expectMsg(Graph.Look(0))
    probe.send(ant, Array(Graph.Edge(1, 20, 0)))
  }

  it should "not go to through the same edge twice" in {
    val probe = TestProbe() //acting as a Graph
    val colonyProbe = TestProbe()
    //stepColony will forward its msgs to the child(Ant)
    val stepColony = system.actorOf(
      Props(new StepParent(Ant.Props(probe.ref, 0), colonyProbe.ref))
    )

    probe.expectMsg(Graph.Look(0))
    stepColony ! Array(Graph.Edge(1, 20, 0)) //only one way to go
    probe.expectMsg(Graph.Travel(0, 1)) //just follow the crumbs
    probe.expectMsg(Graph.Look(1))    //look around the new place
    stepColony ! Array(Graph.Edge(1, 20, 0), Graph.Edge(0, 20, 0)) //it's a loop!
    //Now the ant should NOT go through this edge but assume success and report to parent
    colonyProbe.expectMsg(Ant.TourCompleted(Graph.Tour(40, 0::1::0::Nil)))
  }

}
