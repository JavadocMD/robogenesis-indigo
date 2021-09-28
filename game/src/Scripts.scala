package game

import indigo._
import game.Dialog.Actor
import game.Dialog.Tasks as D
import game.Task as T

object Scripts:

  val AlarmSound = T.Sequence(
    T.Event(Assets.thrum.play),
    T.Delay(Seconds(1.0)),
    T.Event(Assets.thrum.play),
    T.Delay(Seconds(1.0)),
    T.Event(Assets.thrum.play),
    T.Delay(Seconds(1.0))
  )

  val StartConveyor = T.Sequence(
    AlarmSound,
    D.turnOnTheLights(Seconds(3.0)), // TODO: parallel
    T.Event(Assets.machine.play),
    T.InterpolateE(
      Seconds(2.0),
      Interpolate.powInOut(2),
      (alpha, model) =>
        val speed = -100.0 * alpha
        // Have to fire event, because Belts are subsytems and can't read the model directly.
        (model, GameEvent.SetBeltSpeed(speed) :: Nil)
    ),
    T.Then(_.copy(factoryOn = true, selectionOn = true))
  )

  val StopConveyor = T.Sequence(
    T.Then(_.copy(factoryOn = false, selectionOn = false)),
    AlarmSound,
    D.turnOffTheLights(Seconds(3.0)), // TODO: parallel
    T.Event(Assets.machine.play),
    T.InterpolateE(
      Seconds(2.0),
      Interpolate.powInOut(2),
      (alpha, model) =>
        val speed = -100.0 * (1.0 - alpha)
        // Have to fire event, because Belts are subsytems and can't read the model directly.
        (model, GameEvent.SetBeltSpeed(speed) :: Nil)
    )
  )

  val IntroDialog =
    given actor: Actor = Actor.Radio
    T.Sequence(
      D.startDialog,
      D.speak("You there, wake up!\n\n(click to continue...)"),
      D.speak("Can't you see there's junk to sort?\n\n*sigh* You do remember why you're here, don't you?"),
      D.speak("Robogenitors like yourself are tasked with sorting through these scrap piles for useful robot parts."),
      D.speak(
        "Assemble a battle bot. Send it to the front lines.\n\nIf you can manage this, maybe -- just maybe -- we will live to see another day."
      ),
      D.speak(
        "I shouldn't have to tell you how valuable these Battle CPUs are, but just so we're clear, let me say this slowly:\n\nEach. Robogenitor. Gets. ONE."
      ),
      D.speak("Don't waste it.\n\nYour people are counting on you.\n\nNow get to work!"),
      D.endDialog
    )

  val Intro = T.Sequence(
    IntroDialog,
    // TODO: ShowTutorial,
    StartConveyor
  )

end Scripts
