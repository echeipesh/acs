package acs.akka

/**
 * User: eugene
 * Date: 12/8/13
 *
 * Design choices:
 * 1. The acs.akka.Graph manages the weight updates.
 *  This is sufficient for the base implementation and keeps the messages both small and immutable.
 *  A more flexible alternative would be to accept Travel(from, to, f:Edge=>Edge) and use f for update.
 *  As long as f does not close over parent Actor scope it would be safe to use.
 *
 * 2. The graph is basically a matrix, which is a little silly because it might as well be triangle matrix
 *  But in reality this is not the most efficient approach either way and will be refectored when it matters.
 */

import akka.actor.{Props, Actor}
import acs.Params
import acs.Types._

object GraphActor {
  type View = Array[Edge]

  case class Look(at: NodeID)
  case class Travel(from: NodeID, to: NodeID)
  case class Edge(to: NodeID, distance: Double, p_weight: Double)

  case class GlobalUpdate(tour: Tour)
  case object UpdateDone

  def Props(g_dist: Matrix[Double], params:Params):Props = akka.actor.Props(classOf[GraphActor], g_dist, params)
}

/**
 * acs.akka.Graph is assumed to be completely connected, edge exists between every pair of vertices
 */
class GraphActor(G_dist: Matrix[Double], params: Params) extends Actor {
  import GraphActor._
  val G =
    for {from <- 0 until G_dist.length} yield
      (for {to <- 0 until G_dist(from).length} yield
        Edge(to, G_dist(from)(to), 0)
      ).toArray //make this array so we can swap edges in and out


  def receive = {
    case GraphActor.Look(at) => //send back Edges available at given location
      sender ! G(at).filter(_.to != at).toArray

    case GraphActor.Travel(from, to) =>
      G(from)(to) = localTrailUpdate(G(from)(to))
      G(to)(from) = localTrailUpdate(G(to)(from))

    case GraphActor.GlobalUpdate(tour) =>
      for (List(from,to) <- (tour.path sliding 2)) {
        G(from)(to) = globalTrailUpdate(G(from)(to), tour.length)
        G(to)(from) = globalTrailUpdate(G(to)(from), tour.length)
      }
      sender ! UpdateDone
  }


  /**
   * When an acs.akka.Ant uses an edge it changes the pheromone  on that trail
   *
   * The first portion represents trail decay (presumably 'time' passed last time this edge was used)
   * The second portion is the new deposit
   */
  def localTrailUpdate(e: Edge): Edge =
    e.copy(p_weight = (1-params.alpha)*e.p_weight+params.alpha*params.tao_0 )

  /**
   * This rule is used to reward the best tour in the iteration
   */
  def globalTrailUpdate(e: Edge, tourLength:Double): Edge =
    e.copy(p_weight = (1-params.alpha)*e.p_weight+params.alpha*(1/tourLength))
}


