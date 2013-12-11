/**
 * User: eugene
 * Date: 12/8/13
 *
 * Design choices:
 * 1. The Graph manages the weight updates.
 *  This is sufficient for the base implementation and keeps the messages both small and immutable.
 *  A more flexible alternative would be to accept Travel(from, to, f:Edge=>Edge) and use f for update.
 *  As long as f does not close over parent Actor scope it would be safe to use.
 *
 * 2. The graph is basically a matrix, which is a little silly because it might as well be triangle matrix
 *  But in reality this is not the most efficient approach either way and will be refectored when it matters.
 */

import akka.actor.{Props, ActorRef, Actor}

object Graph {
  type NodeID = Int
  /** Used to control the rate of change in pheromone deposits */
  val alpha = 0.1
  /** weighs the relative importance of closeness vs pheromone trail in choices*/
  val beta = 1
  /** base for local trail updating*/
  val tao_0 = 1

  /** useful for something */
  val q_0 = 0.5

  case class Look(at: NodeID)
  type View = Array[Edge]
  case class Travel(from: NodeID, to: NodeID)
  case class Edge(to: NodeID, distance: Double, p_weight: Double)

  case class Tour(length: Double, path: List[Graph.NodeID])
  case class GlobalUpdate(tour: Tour)
  case object UpdateDone

  /**
   * When an Ant uses an edge it changes the pheromone  on that trail
   *
   * The first portion represents trail decay (presumably 'time' passed last time this edge was used)
   * The second portion is the new deposit
   */
  def localTrailUpdate(e: Edge): Edge =
    e.copy(p_weight = (1-alpha)*e.p_weight+alpha*tao_0 )

  /**
   * This rule is used to reward the best tour in the iteration
   */
  def globalTrailUpdate(e: Edge, tourLength:Double): Edge =
    e.copy(p_weight = (1-alpha)*e.p_weight+alpha*(1/tourLength))

  def Props(g_dist: Array[Array[Double]]):Props = akka.actor.Props(classOf[Graph], g_dist)
}

/**
 * Graph is assumed to be completely connected, edge exists between every pair of vertices
 */
class Graph(G_dist: Array[Array[Double]]) extends Actor {
  import Graph._
  val G =
    for {from <- 0 until G_dist.length} yield
      (for {to <- 0 until G_dist(from).length} yield
        Edge(to, G_dist(from)(to), 0)
      ).toArray //make this array so we can swap edges in and out


  def receive = {
    case Graph.Look(at) => //send back Edges available at given location
      sender ! G(at).filter(_.to != at).toArray

    case Graph.Travel(from, to) =>
      //This just shows I shouldn't be keeping the full matrix, but rather only one triangle of it
      G(from)(to) = localTrailUpdate(G(from)(to))
      G(to)(from) = localTrailUpdate(G(to)(from))

    case Graph.GlobalUpdate(tour) =>
      for (List(from,to) <- (tour.path sliding 2)) {
        G(from)(to) = globalTrailUpdate(G(from)(to), tour.length)
        G(to)(from) = globalTrailUpdate(G(to)(from), tour.length)
      }
      sender ! UpdateDone
  }
}


