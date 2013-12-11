/**
 * User: eugene
 * Date: 12/8/13
 *
 * Design choices:
 * 1. The Graph manages the weight updates.
 *  This is sufficient for the base implementation and keeps the messages both small and immutable.
 *  A more flexible alternative would be to accept Travel(from, to, f:Edge=>Edge) and use f for update.
 *  As long as f does not close over parent Actor scope it would be safe to use.
 */

import akka.actor.{Props, ActorRef, Actor}

object Graph {
  type NodeID = Int
  /** Used to control the rate of change in pheromone deposits */
  val alpha = 0.1

  /** base for local trail updating*/
  val tao_0 = 1

  case class Look(at: NodeID)
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
  def localTrailUpdate(e: Edge): Edge = e.copy(p_weight = (1-alpha)*e.p_weight+alpha*tao_0 )

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
      G(from)(to) = localTrailUpdate(G(from)(to))

    case Graph.GlobalUpdate(tour) =>
      sender ! UpdateDone //Total lie
  }
}


