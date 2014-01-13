package acs.serial

import acs.Types._
import acs.TspData

/**
 * User: eugene
 * Date: 1/13/14
 */
object Main extends App {

  //Optimal tour of XQF131: 564
  val g:Matrix[Double] = TspData.readTspFile("XQF131.dat")
  val s = new ACS(g)

  for (i <- 1 to 1000) {
    val tour = s.iteration(10)
    println(s"iteration($i): " + tour.length + " BEST: "+ s.best.length)
  }
}
