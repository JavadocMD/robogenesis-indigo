package game

import indigo._

case class Task(
    duration: Seconds,
    update: (Double, Model) => Outcome[Model],
    elapsed: Seconds = Seconds.zero
)

object Task {
  def onComplete(duration: Seconds, complete: Model => Outcome[Model]): Task =
    Task(duration, (a, model) => if (a < 1.0) Outcome(model) else complete(model))

  def update(model: Model, delta: Seconds): Outcome[Model] = {
    var tasks = List.empty[Task]
    model.tasks.foldLeft(Outcome(model)) { (oc, t) =>
      // Calculate time for this task.
      val nextElapsed = t.elapsed + delta
      val alpha       = (nextElapsed / t.duration).toDouble.min(1.0)
      if (nextElapsed < t.duration) {
        // if task is still running, keep t in our list for next frame
        tasks ::= t.copy(elapsed = nextElapsed)
      }
      // Perform update.
      oc.flatMap(m => t.update(alpha, m))
    } map { m =>
      m.copy(tasks = tasks)
    }
  }
}
