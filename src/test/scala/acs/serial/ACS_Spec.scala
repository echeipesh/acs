package acs.serial

import org.scalatest._
import acs.TestGraphs
import acs.Types._

/**
 * User: eugene
 * Date: 1/12/14
 */
class ACS_Spec extends FlatSpec with Matchers {

  "ACS" should "be constructable" in {
    val acs = new ACS(TestGraphs.AB)
  }

  it should "be able to perform a single iteration" in {
    val acs = new ACS(TestGraphs.AB)

    val tour = acs.iteration(m = 1)

    tour should equal ( Tour(20,List(0,1,0)) )
  }

  it should "expect to use Params to calculate NN" in {
    acs.Params.nearestNeighborTour(TestGraphs.ABCD_perimeter) should equal (20)
  }

  it should "be able to choose by PDF" in {
    val choices = List(1,2,3,4,5)
    val picks = for (x <- 1 to 1000) yield ACS.chooseByPDF(choices){it => if (it == 3) 4 else 1}

    //in reality we expect this to be around 500
    picks.count( _ == 3) should be > 450
    picks.count( _ == 3) should be < 550

    picks.count( _ == 1) should be > 50
    picks.count( _ == 1) should be < 150
  }

  it should "solve box Graph" in {
    val s = new ACS(TestGraphs.ABCD_perimeter)

    for (i <- 1 to 5) {
      s.iteration(3)
    }
    println(s.best)
    s.best.length should be (20)
  }
}
