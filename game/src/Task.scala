package game

import indigo._
import scala.annotation.tailrec

trait Task:
  def update(model: Model, delta: Seconds)(using FrameContext[GameData]): Task.Result

object Task:

  // The result of running a task.
  // A task can complete, consuming some amount of frame delta,
  // or require continuation next frame (implicitly consuming all of the frame's delta).
  sealed trait Result
  object Result:
    case class Complete(delta: Seconds, model: Model, events: List[GlobalEvent] = Nil) extends Result
    case class Continue(task: Task, model: Model, events: List[GlobalEvent] = Nil)     extends Result

  import Result._

  def update(context: FrameContext[GameData], model: Model): Outcome[Model] =
    given FrameContext[GameData] = context

    var currModel  = model
    var currEvents = List.empty[GlobalEvent]
    var nextTasks  = List.empty[Task]

    // Run (root level) tasks in parallel. Drop any that complete.
    for t <- model.tasks do
      t.update(currModel, context.delta) match
        case Complete(_, m, es) =>
          currModel = m
          currEvents ++= es
        case Continue(t, m, es) =>
          currModel = m
          currEvents ++= es
          nextTasks ::= t

    Outcome(currModel.copy(tasks = nextTasks))
      .addGlobalEvents(currEvents)
  end update

  // Immediately alters the model without consuming any frame delta.
  case class Then(f: Model => Model) extends Task:
    def update(model: Model, delta: Seconds)(using FrameContext[GameData]) =
      Complete(delta, f(model))

  // Immediately alters the model and produces events without consuming any frame delta.
  case class ThenE(f: Model => (Model, List[GlobalEvent])) extends Task:
    def update(model: Model, delta: Seconds)(using FrameContext[GameData]) =
      val (m, es) = f(model)
      Complete(delta, m, es)

  // Produces static events without altering the model or consuming any frame delta.
  case class Event(events: GlobalEvent*) extends Task:
    val eventsList = events.toList
    def update(model: Model, delta: Seconds)(using FrameContext[GameData]) =
      Complete(delta, model, eventsList)

  // Wait for time to pass. Does not alter the model.
  case class Delay(duration: Seconds, elapsed: Seconds = Seconds.zero) extends Task:
    def update(model: Model, delta: Seconds)(using FrameContext[GameData]) =
      val nextElapsed = elapsed + delta
      if nextElapsed >= duration then Complete(nextElapsed - duration, model)
      else Continue(copy(elapsed = nextElapsed), model)

  // Alter the model over time with the help of an interpolated value.
  case class Interpolate(
      duration: Seconds,
      interpolation: Double => Double,
      f: (Double, Model) => Model,
      elapsed: Seconds = Seconds.zero
  ) extends Task:
    def update(model: Model, delta: Seconds)(using FrameContext[GameData]) =
      val nextElapsed = elapsed + delta
      val baseAlpha   = (nextElapsed / duration).toDouble.min(1.0)
      val alpha       = interpolation(baseAlpha)
      val nextModel   = f(alpha, model)
      if nextElapsed >= duration then Complete(nextElapsed - duration, nextModel)
      else Continue(copy(elapsed = nextElapsed), nextModel)

  // Run tasks in sequence.
  case class Sequence(taskSequence: List[Task]) extends Task:
    // Each update, run as many tasks in sequence as we can until:
    // 1. all tasks are complete, or
    // 2. all of the delta for this frame has been consumed.
    def update(model0: Model, delta0: Seconds)(using FrameContext[GameData]) =
      @tailrec
      def recurse(tasks: List[Task], delta: Seconds, model: Model, events: List[GlobalEvent]): Result =
        if tasks.isEmpty then
          // Sequence complete
          Complete(delta, model, events)
        else if delta == Seconds.zero then
          // No more delta
          Continue(copy(taskSequence = tasks), model, events)
        else
          // Apply delta to the head task...
          tasks.head.update(model, delta) match
            // Head task still processing, continue next frame.
            case Continue(t, m, es) =>
              Continue(copy(taskSequence = t :: tasks.tail), m, es ++ events)
            // Head task done, recurse over the tail!
            case Complete(d, m, es) =>
              recurse(tasks.tail, d, m, es ++ events)

      recurse(taskSequence, delta0, model0, Nil)

  object Sequence:
    def apply(ts: Task*): Sequence = Sequence(ts.toList)

end Task
