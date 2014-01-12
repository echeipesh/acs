package acs.serial

import org.scalatest._

/**
 * User: eugene
 * Date: 1/12/14
 */
class ACS_Spec extends FlatSpec with Matchers {
  val g = acs.TestGraphs.AB

  "ACS" should "be constructable" in {
    val asc = new ASC(g)
  }
}
