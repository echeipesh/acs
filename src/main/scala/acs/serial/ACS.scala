package acs.serial

import acs.TspData.Matrix
import acs.Params

/**
 * Ant Colony System
 * User: eugene
 * Date: 1/12/14
 */
class ACS(G: Matrix[Double], p: Params) {
  def this(G:Matrix[Double]) = this(G, Params.forGraph(G))

}
