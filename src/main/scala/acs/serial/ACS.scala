package acs.serial

import acs.Types._
import acs.Params

object ACS{

  /**
   * @param from sequence of items to choose from
   * @param weight function that gives weight for an item
   * @return random weighted uniform selection from list
   */
  def chooseByPDF[T](from: Seq[T])(weight: T=>Double):T = {
    var sum:Double = 0
    val list = for (t <- from) yield {
      val w = weight(t)
      sum += w
      w -> t
    }

    val threshold = util.Random.nextDouble() * sum
    var level = 0.0

    for ( tuple <- util.Random.shuffle(list)) {
      level += tuple._1
      if (level >= threshold) return tuple._2
    }
    throw new Exception("unreachable code reached in chooseByPDF")
  }
}


/**
 * Ant Colony System.
 * Maintains the state of exploration graph G in Pheromone matrix.
 * Best tour is always available in .best
 * Trigger iteration of ACS search by calling .iteration()
 *
 * @param G Edge length matrix for graph being explored (read-only)
 * @param p Params to ACS (Optional)
 */
class ACS(G: Matrix[Double], p: Params) {
  def this(G:Matrix[Double]) = this(G, Params.forGraph(G))
  /** Pheromone Matrix
    * Init to tao_0*alpha so that choose_greedy does not reduce all choices to 0
    * This is equivalent to localUpdate by a single ant on edge of value 0
    */
  val P = Array.fill[Double](G.length, G.length) {p.tao_0*p.alpha}
  var best:Tour = Tour(Double.MaxValue, List(0))

  /**
   * Perform one iteration of the ACS: deploy all the ants and return the best tour
   * @param m number of ants to spawn
   * @return best tour in this iteration
   */
  def iteration(m: Int = 10):Tour = {
    val tours = for (i <- 1 to 10) yield { antTraverse}
    val iteration_best = tours.minBy(_.length)
    globalUpdate(iteration_best)
    if (iteration_best.length < best.length) best = iteration_best
    iteration_best
  }

  /**
   * Single ant, responsible for traversing the graph(G) and performing localUpdates along the way.
   * Ant starts at a random city.
   */
  def antTraverse:Tour = {
    val start = util.Random.nextInt(G.length)
    var path:List[NodeID] = List(start)
    var distance:Double = 0

    /** Select next node to visit as defined by ACS algorithm */
    def chooseNext(choices: IndexedSeq[NodeID]):NodeID = {
      def weight(from: NodeID, to: NodeID) = P(from)(to) * math.pow( 1/G(from)(to), p.beta)

      if (util.Random.nextDouble() <= p.q_0){
        choices.maxBy(there => weight(path.head, there))
      }else{
        ACS.chooseByPDF(choices){there => weight(path.head, there)}
      }
    }

    //we always find a path to new node in fully-connected graph until the last edge
    for (i <- 1 until G.length) {
      val fresh = (0 until G.length).filter{i => !path.contains( i ) }
      val nextNode = chooseNext(fresh)
      localUpdate(path.head, nextNode)
      distance += G(path.head)(nextNode)
      path = nextNode :: path
    }
    //final leap is to the start
    val t = Tour(distance + G(path.head)(start), start :: path)
    t
  }

  /** "Local updating is intended to avoid a very strong edge being chosen by all the ants" */
  def localUpdate(from: NodeID, to: NodeID):Unit = {
    // tiny increase, 10% decrease + another increase, basically hovers at value of 10 ants
    P(from)(to) = (1-p.alpha)*P(from)(to) + p.alpha*p.tao_0
    P(to)(from) = P(from)(to)
  }

  /** "Global updating is intended to reward edges belonging to shorter tours" */
  def globalUpdate(tour:Tour):Unit = {
    for (List(from,to) <- tour.path sliding 2) {
      P(from)(to) = (1-p.alpha)*P(from)(to) + p.alpha*(1/tour.length)
      P(to)(from) = P(from)(to)
    }
  }
}
