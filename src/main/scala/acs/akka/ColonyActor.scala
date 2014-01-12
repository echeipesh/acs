package acs.akka

/**
 * User: eugene
 * Date: 12/8/13
 */


import akka.actor._
import akka.actor.Props
import acs.Params
import acs.Types._

object ColonyActor {
  /** Start a cycle with given number of ants */
  case class Start(ants: Int)
  case object GiveBestTour
  def Props(g: Matrix[Double], params: Params):Props = akka.actor.Props(classOf[ColonyActor], g, params)
}
/**
 * What do we do here?
 * - spawn ants to traverse the graph
 * - collect their traversals (wait for all)
 * - perform a global update
 *
 * We can be in several states:
 * waiting: Nothing much is going on
 *  - Start a wave
 *  - Give current best tour
 *
 * exploring: The ants, they are crawling
 *  - receive TourCompleted and update the tours
 *  - receive UpdateDone in response to Global update
 *      only to best sent out after all the ants return
 *
 * @param G Actor representing a acs.akka.Graph we are traversing
 */
class ColonyActor(G_dist: Matrix[Double], params: Params) extends Actor {
  import ColonyActor._

  val G = context.actorOf( GraphActor.Props(G_dist, params) )

  //This should probably be Option[Tour]
  var ants:Set[ActorRef] = Set.empty
  var bestTour: Tour = Tour(Double.MaxValue, Nil)

  def receive = waiting

  val waiting: Receive = {
    case ColonyActor.Start(n) =>
      context.become(running)
      UnleashTheAnts(n)

    case GiveBestTour =>
      sender ! bestTour
  }

  val running: Receive = {
    case AntActor.TourCompleted(tour: Tour) =>
      ants -= sender
      if (tour.length < bestTour.length)
        bestTour = tour

      if (ants.isEmpty)
        G ! GraphActor.GlobalUpdate(bestTour)

    case GraphActor.UpdateDone  =>
      //Once graph is consistent with final state we notify parent of the best tour we found
      context.become(waiting)
      context.parent ! bestTour
  }

  def UnleashTheAnts(n: Int):Unit = {
    for (i <- 1 to n){
      ants += context.actorOf( AntActor.Props(G, 0, params))
    }
  }
}
