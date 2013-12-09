import akka.actor.{ActorRef, Actor}
import Graph._

/**
 * User: eugene
 * Date: 12/8/13
 */

object Ant{
  case class TourCompleted(nodes: List[NodeID])
}
class Ant(G: ActorRef, start: Graph.NodeID) extends Actor {

  val rng = scala.util.Random

  G ! Graph.Look(start)
  var path:List[NodeID] = start :: Nil //path traveled, in reverse order

  def receive = {
    case view:Array[Edge] => //got a response to Look
      val freshView = view.filter(e=> !path.contains(e))

      if (freshView.isEmpty){ //SUCCESS! nothing else to see
        context.parent ! Ant.TourCompleted(start :: path) //recall G is fully connected

      }else{ //We have fresh nodes to explore, onward!
        val chosen = chooseEdge(freshView)
        G ! Graph.Travel(path.head, chosen.to)
        path = chosen.to :: path
      }
  }

  def chooseEdge(view: Array[Edge]): Edge ={
    view( rng.nextInt(view.length) )
  }
}
