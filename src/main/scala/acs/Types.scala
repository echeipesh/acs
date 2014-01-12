package acs

/**
 * User: eugene
 * Date: 1/12/14
 */
object Types {
  /**
   * val m:Matrix[Double]
   * Operation supported:
   * - item in fromX, toY = m(fromX)(toY)
   * - all items from X = m(fromX)
   * @param T
   */
  type Matrix[T] = Array[Array[T]]

  type NodeID = Int

  case class Tour(length: Double, path: List[NodeID])

}
