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

  class Tap(probe: ActorRef) extends Actor {
    def receive = {
      case msg => probe.tell(msg, sender)
    }
  }

//  "Ant" should "send Travel in response to view update" in {
//    val antProbe = TestProbe()
//    //this will error out
//    val tap = system.actorOf( Props(classOf[Tap], antProbe.ref), "TappedAnt" )
//    val ant =  system.actorOf( Props(classOf[Ant], tap))
//    antProbe.expectMsg(Look(0))
//    antProbe.forward(G, Look(0))
//  }

}
