/**
 * User: eugene
 * Date: 12/9/13
 */

import akka.actor.{Props, ActorSystem}
import akka.testkit.TestProbe
import org.scalatest.{BeforeAndAfterAll, Matchers, FlatSpec}

class ColonySpec extends FlatSpec with Matchers with BeforeAndAfterAll {
  implicit val system = ActorSystem("TestSys")
  override def afterAll()  { system.shutdown() }

  val G_dist:Array[Array[Double]] = Array(
    Array( 0.0, 10.0, 20.0),
    Array(10.0,  0.0,  5.0),
    Array(20.0,  5.0,  0.0)
  )
  val G = system.actorOf( Props(classOf[Graph], G_dist), "Graph")
  val C = system.actorOf( Props(classOf[Colony], G, 2), "Colony")
  val probe = TestProbe()

  "Colony" should "solve TSP" in {
    assert(false) //but does not :(
  }
}
