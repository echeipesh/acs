/**
 * User: eugene
 * Date: 12/8/13
 */

import akka.actor._

/**
 * What do we do here?
 * - spawn ants to traverse the graph
 * - collect their traversals (wait for all)
 * - perform a global update
 *
 * @param G Actor representing a Graph we are traversing
 */
class Colony(G: ActorRef, antCount: Int) extends Actor {
  var children = Set.empty[ActorRef]
  var tours = Set.empty[List[Graph.NodeID]]

  UnleashTheAnts()

  def receive: Actor.Receive = {
    case Ant.TourCompleted(path) =>
      children -= sender
      tours += path
      if (children.isEmpty)
        G ! Graph.GlobalUpdate(tours)

    case Graph.UpdateDone  =>
      children = Set.empty[ActorRef]
      tours = Set.empty[List[Graph.NodeID]]
      UnleashTheAnts()
  }

  def UnleashTheAnts():Unit = {
    for (i <- 1 to antCount) {
      children += context.actorOf( Props(classOf[Ant], G, 0), s"Ant_$i")
    }
  }
}
