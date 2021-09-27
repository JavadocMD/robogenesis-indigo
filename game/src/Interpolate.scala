package game

import scala.math.pow

object Interpolate:
  opaque type Interpolation = Double => Double
  extension (f: Interpolation) def apply(alpha: Double): Double = f(alpha)

  def powIn(power: Int): Interpolation =
    (alpha: Double) => pow(alpha, power)

  def powOut(power: Int): Interpolation =
    val sign = if power % 2 == 0 then -1.0 else 1.0
    (alpha: Double) => 1.0 + sign * pow(alpha - 1.0, power)

  def powInOut(power: Int): Interpolation =
    val factor = (if power % 2 == 0 then -1.0 else 1.0) / 2.0
    (alpha: Double) =>
      if alpha < 0.5 then pow(alpha * 2.0, power) / 2.0
      else 1.0 + factor * pow(2.0 * (alpha - 1.0), power)
