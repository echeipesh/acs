package acs.serial

import acs.Types._
import acs.Params

/**
 * Ant Colony System
 * User: eugene
 * Date: 1/12/14
 */
class ACS(G: Matrix[Double], p: Params) {
  def this(G:Matrix[Double]) = this(G, Params.forGraph(G))

  def iteration(ants: Int = 10):Tour = ???

}
