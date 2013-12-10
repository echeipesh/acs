/**
 * User: eugene
 * Date: 12/9/13
 */

import akka.actor._
import akka.testkit.TestProbe
import org.scalatest._
import Graph._

class AntSpec extends FlatSpec with Matchers with BeforeAndAfterAll {
  implicit val system = ActorSystem("TestSys")
  override def afterAll()  { system.shutdown() }

  val G_dist:Array[Array[Double]] = Array(
    Array( 0.0, 10.0, 20.0),
    Array(10.0,  0.0,  5.0),
    Array(20.0,  5.0,  0.0)
  )
  val G = system.actorOf( Props(classOf[Graph], G_dist), "GraphActor")

  "Ant" should "send Travel in response to view update" in {
    val probe = TestProbe()
    val ant =  system.actorOf( Props(classOf[Ant], probe.ref, 0), "TestAnt")
    probe.expectMsg(Look(0))
    probe.forward(G)
  }

}
