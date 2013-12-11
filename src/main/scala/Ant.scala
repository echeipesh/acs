/**
 * User: eugene
 * Date: 12/8/13
 */

import akka.actor.{Props, ActorRef, Actor}

object Ant{
  case class TourCompleted(tour: Graph.Tour)
  def Props(g: ActorRef, start: Graph.NodeID):Props = akka.actor.Props(classOf[Ant], g, start)
}

/**
 * Ant will traverse G starting from start until it will find new nodes it has not visited.
 * At that point it will assume the G is connected and travel home, reporting and stopping itself.
 * @param G the graph Ant is exploring
 * @param start index of the starting node (0-indexed)
 */
class Ant(G: ActorRef, start: Graph.NodeID) extends Actor {
  import Graph._

  //path traveled, in reverse order
  var path:List[NodeID] = start :: Nil
  var distanceCovered:Double = 0
  val rng = scala.util.Random

  G ! Graph.Look(start)

  def receive = {
    case view:Array[Edge] => //got a response to Look
      val freshView = view.filter(edge=> !path.contains(edge.to))

      if (freshView.isEmpty){
        //SUCCESS! nothing else to see
        //recall G is fully connected, so we can always return to start
        context.parent ! Ant.TourCompleted(Graph.Tour(distanceCovered, start :: path))
        context.stop(self)

      }else{ //We have fresh nodes to explore, onward!
        val chosen = chooseEdge(freshView)
        distanceCovered += chosen.distance
        println(s"going to $chosen")
        G ! Graph.Travel(path.head, chosen.to)
        G ! Graph.Look(chosen.to)
        path = chosen.to :: path
      }
  }

  def chooseEdge(view: Array[Edge]): Edge ={
    //yeah, this isn't actually the real way
    view( rng.nextInt(view.length) )
  }
}
