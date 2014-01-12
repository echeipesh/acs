package acs

import TspData.Matrix

/**
 * User: eugene
 * Date: 1/12/14
 */
object TestGraphs {

  /**
   * @return G(3 nodes, all edges are 10)
   */
  def ABC:Matrix[Double] = Array(
    //       A,    B,    C
    Array( 0.0, 10.0, 10.0),
    Array(10.0,  0.0, 10.0),
    Array(10.0, 10.0,  0.0)
  )

  /**
   * @return G(2 nodes, all edges are 10)
   */
  def AB:Matrix[Double] = Array(
    //       A,    B
    Array( 0.0, 10.0),
    Array(10.0,  0.0)
  )


}
