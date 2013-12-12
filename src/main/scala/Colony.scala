/**
 * User: eugene
 * Date: 12/8/13
 */

import akka.actor._
import akka.actor.Props

object Colony {
  /** Start a cycle with given number of ants */
  case class Start(ants: Int)
  case object GiveBestTour
  def Props(g: ActorRef):Props = akka.actor.Props(classOf[Colony], g)
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
 *  - buffer start request
 *  - receive TourCompleted and update the tours
 *  - receieve UpdateDone in response to Global update
 *      only to best sent out after all the ants return
 *
 * @param G Actor representing a Graph we are traversing
 */
class Colony(G: ActorRef) extends Actor {
  import Colony._

  var children: Set[ActorRef] = Set.empty
  //This should probably be Option[Tour]
  var bestTour: Graph.Tour = Graph.Tour(Double.MaxValue, Nil)

  def receive = waiting

  val waiting: Receive = {
    case Colony.Start(n) =>
      children = Set.empty
      context.become(running)
      UnleashTheAnts(n)

    case GiveBestTour =>
      sender ! bestTour
  }

  val running: Receive = {
    case Ant.TourCompleted(tour: Graph.Tour) =>
      children -= sender
      if (tour.length < bestTour.length)
        bestTour = tour

      if (children.isEmpty)
        G ! Graph.GlobalUpdate(bestTour)

    case Graph.UpdateDone  =>
      //Once graph is consistent with final state we notify parent of the best tour we found
      context.become(waiting)
      context.parent ! bestTour
  }

  def UnleashTheAnts(n: Int):Unit = {
    for (i <- 1 to n) {
      children += context.actorOf( Ant.Props(G, 0))
    }
  }
}
