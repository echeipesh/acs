package acs.akka

/**
 * User: eugene
 * Date: 12/9/13
 */

import acs.akka.{GraphActor, AntActor}
import akka.actor._
import akka.testkit.TestProbe
import org.scalatest._
import acs.Params


class AntSpec extends FlatSpec with Matchers with BeforeAndAfterAll {
  implicit val system = ActorSystem("TestSys")
  override def afterAll()  { system.shutdown() }

  val G_dist:GraphActor.Matrix[Double] = Array(
    Array( 0.0, 10.0, 20.0),
    Array(10.0,  0.0,  5.0),
    Array(20.0,  5.0,  0.0)
  )
  val G = system.actorOf( GraphActor.Props(G_dist, Params.default), "GraphActor")

  "acs.akka.Ant" should "send Travel in response to view update" in {
    val probe = TestProbe()
    val ant =  system.actorOf( AntActor.Props(probe.ref, 0, Params.default))
    probe.expectMsg(GraphActor.Look(0))
    probe.send(ant, Array(GraphActor.Edge(1, 20, 0)))
  }

  it should "not go to through the same edge twice" in {
    val probe = TestProbe() //acting as a acs.akka.Graph
    val colonyProbe = TestProbe()
    //stepColony will forward its msgs to the child(acs.akka.Ant)
    val stepColony = system.actorOf(
      Props(new StepParent(AntActor.Props(probe.ref, 0, Params.default), colonyProbe.ref))
    )

    probe.expectMsg(GraphActor.Look(0))
    stepColony ! Array(GraphActor.Edge(1, 20, 0)) //only one way to go
    probe.expectMsg(GraphActor.Travel(0, 1)) //just follow the crumbs
    probe.expectMsg(GraphActor.Look(1))    //look around the new place
    stepColony ! Array(GraphActor.Edge(1, 20, 0), GraphActor.Edge(0, 20, 0)) //it's a loop!
    //Now the ant should NOT go through this edge but assume success and report to parent
    colonyProbe.expectMsg(AntActor.TourCompleted(GraphActor.Tour(40, 0::1::0::Nil)))
  }

}
