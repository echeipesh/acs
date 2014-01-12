package acs.akka

/**
 * User: eugene
 * Date: 12/8/13
 *
 * Design Decisions:
 * 1. Notice that we ask for a View to make our own choice where to go.
 *  We could in reality just ask the graph to make the choice for us and let us know.
 *  However in this manner we can have ants following multiple decision algorithms,
 *  which is something I'm curious to try later :)
 */

import GraphActor._
import akka.actor.{Props, ActorRef, Actor}
import acs.Params
import acs.Types._

object AntActor{
  case class TourCompleted(tour: Tour)
  def Props(g: ActorRef, start: NodeID, params: Params):Props = akka.actor.Props(classOf[AntActor], g, start, params)
}

/**
 * acs.akka.Ant will traverse G starting from start until it will find new nodes it has not visited.
 * At that point it will assume the G is connected and travel home, reporting and stopping itself.
 * @param G the graph acs.akka.Ant is exploring
 * @param start index of the starting node (0-indexed)
 */
class AntActor(G: ActorRef, start: NodeID, params: Params) extends Actor {

  //path traveled, in reverse order
  var path:List[NodeID] = start :: Nil
  var distanceCovered:Double = 0
  val rng = scala.util.Random

  G ! GraphActor.Look(start)

  def receive = {
    case view:GraphActor.View => //got a response to Look
      val freshView = view.filter(edge=> !path.contains(edge.to))

      if (! freshView.isEmpty){
        val chosen = chooseEdge(freshView)
        takeEdge(chosen)

      }else{
        //SUCCESS! nothing else to see
        //recall G is fully connected, so we can always return to start
        val edgeHome = view.filter(_.to == start).head
        takeEdge(edgeHome)
        context.parent ! AntActor.TourCompleted(Tour(distanceCovered,path))
        context.stop(self)
      }
  }

  def chooseEdge(view: GraphActor.View): Edge ={
    def chooseWeighted(xs: Seq[(GraphActor.Edge, Double)]):Edge = {
      val total = xs.map(_._2).sum
      val xsr = rng.shuffle(xs.toSeq)
      val target = rng.nextDouble() * total
      val it = xsr.iterator
      var x = it.next()
      var acc = x._2
      while (acc < target){ //hasNext is assumed by target value
        x = it.next()
        acc += x._2
      }
      x._1
    }

    val q = rng.nextDouble()
    val ar = view.map(e => (e, e.p_weight * math.pow(1/e.distance, params.beta)))

    if (q <= params.q_0)
      //we select the trail with strongest pheromone to distance ratio
      ar.maxBy(_._2)._1
    else{
      chooseWeighted(ar)
    }
  }

  def takeEdge(chosen:Edge) = {
    val from = path.head
    path = chosen.to :: path
    distanceCovered += chosen.distance
    G ! GraphActor.Travel(from, chosen.to)
    if (chosen.to != start) //no point if we're finished a tour
      G ! GraphActor.Look(chosen.to)
  }

}
