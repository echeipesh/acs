/**
 * User: eugene
 * Date: 12/9/13
 */

import akka.actor._
import akka.testkit._
import org.scalatest._
import scala.concurrent.duration._

class ColonySpec extends FlatSpec with Matchers with BeforeAndAfterAll {
  implicit val system = ActorSystem("TestSys")
  override def afterAll()  { system.shutdown() }

  //We need at least 4 nodes to test TSP meaningfully
  //We'd exepect A-D and C-B routes to be avoided
  val G_dist:Graph.Matrix[Double] = Array(
    //       A,    B,    C,    D
    Array( 0.0,  5.0,  5.0, 20.0),
    Array( 5.0,  0.0, 20.0,  5.0),
    Array( 5.0, 20.0,  0.0,  5.0),
    Array(20.0,  5.0,  5.0,  0.0)
  )
  val G = system.actorOf( Graph.Props(G_dist), "Graph")

  "Colony" should "solve TSP" in {
    val probe = TestProbe()

    //Colony is going to launch ants as soon as it's constructed
    //it should report to it's parent
    val sp = system.actorOf(
      Props(new StepParent(Colony.Props(G), probe.ref)), "controller"
    )
    sp ! Colony.Start(5)
    val tour = probe.expectMsgPF(1.second){case x: Graph.Tour => x}

    //between 5 ants it should be hard not to find the optimal here
    tour.length should equal (20.0)
    tour.path.toSet should equal (List(0, 1, 3, 2, 0).toSet)
    tour.path.length should equal (5)
  }
}
