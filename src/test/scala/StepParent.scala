/**
* User: eugene
* Date: 12/10/13
*/
import akka.actor._

/**
 * Spawn an Actor described by props as a child, report the communication to the probe.
 * All messages not from the child will be forwarded to the child
 * @param child Props to make the child
 * @param probe probe to report to
 */
class StepParent(child: Props, probe: ActorRef) extends Actor {
  val step_child = context.actorOf(child)

  def receive = {
    //Forward messages from the child to the probe
    case msg if sender == step_child =>
      probe.tell(msg, sender)

    //All else must be intended for the child to see
    case msg =>
      step_child ! msg
  }
}