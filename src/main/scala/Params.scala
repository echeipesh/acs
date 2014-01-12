/**
 * User: eugene
 * Date: 12/12/13
 */

object Params {
  def default = Params(0.1, 1, 0.1, 0.9)
  def forGraph(G_dist: Graph.Matrix[Double]) = {
    Params(
      alpha = 0.1,
      beta = 2,
      tao_0 = G_dist.length * Graph.nearestNeighborTour(G_dist),
      q_0 = 0.9
    )
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