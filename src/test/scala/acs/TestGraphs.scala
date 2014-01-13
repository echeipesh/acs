package acs

import Types._

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

  /**
   * G with perimeter edges costing 5 and cross edges (B-C, A-D) costing 20
   * @return
   */
  def ABCD_perimeter:Matrix[Double] = Array(
    //       A,    B,    C,    D
    Array( 0.0,  5.0,  5.0, 20.0),
    Array( 5.0,  0.0, 20.0,  5.0),
    Array( 5.0, 20.0,  0.0,  5.0),
    Array(20.0,  5.0,  5.0,  0.0)
  )


}
