package acs

import Types._
import scala.annotation.tailrec

/**
 * User: eugene
 * Date: 12/12/13
 */
object Params {
  def default = Params(0.1, 1, 0.1, 0.9)
  def forGraph(G_dist: Matrix[Double]) = {
    Params(
      alpha = 0.1,
      beta = 2,
      tao_0 = G_dist.length * nearestNeighborTour(G_dist),
      q_0 = 0.9
    )
  }

  /**
   * Needed as part of heuristic for default ACS parameters
   *
   * @param G_dist matrix of edge lengths in a fully connected graph
   * @return length of nearest neighbor tour starting at node 0
   */
  private def nearestNeighborTour(G_dist: Matrix[Double]):Double = {
    @tailrec
    def nn (at: Int, path: List[Long], tourLength:Double):Double = {
      //make lists of next possible nodes
      val nexts = for {
        to <- 0 until G_dist(at).length
        if to != at
        if ! path.contains(to)
      } yield to -> G_dist(at)(to)

      if (nexts.isEmpty)
        tourLength
      else{
        //choose the one with lowest distance (folding left)
        val next = ( (0 -> Double.MaxValue) /: nexts ){
          case (best, (to, dist)) =>
            if (best._2 < dist)
              best
            else
              (to, dist)
        }

        //return the length of the tour with chose + whatever length we choose after that
        nn(next._1, next._1 :: path, next._2 + tourLength)
      }
    }

    nn(0, Nil, 0)
  }
}

/**
 *
 * @param alpha Used to control the rate of change in pheromone deposits
 * @param beta  weighs the relative importance of closeness vs pheromone trail in choices
 * @param tao_0 base for local trail updating
 * @param q_0 preferences for greedy best-edge selection instead of probabilistic selection
 */
case class Params (
  alpha:Double,
  beta:Double,
  tao_0:Double,
  q_0:Double
)