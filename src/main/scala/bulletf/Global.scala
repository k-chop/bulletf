package bulletf


import scala.collection.mutable
import ui._

object Global {

  private[Global] trait GlobalPool[T] {
    protected[this] var self: mutable.ListBuffer[T]
    def set(t: T) {
      self += t
    }
    def set(t: T*) {
      self ++= t
    }
    def clear() {
      self.clear()
    }
    def fetch(): List[T] = {
      val ret = self.toList
      clear()
      ret
    }
    def nonEmpty: Boolean = self.nonEmpty
  }

  object scoreboard {
    private[this] var self: ScoreBoard = NullScoreBoard

    def set(s: ScoreBoard) {
      self = if (s == null) NullScoreBoard else s
    }

    def get = self.get

    def add(i: Int) {
      self.add(i)
    }

    def reset() {
      self = NullScoreBoard
    }

  }

  object effect_pool extends GlobalPool[Effect] {
    protected[this] var self = mutable.ListBuffer.empty[Effect]
  }

  object enemy_pool extends GlobalPool[Enemy] {
    protected[this] var self = mutable.ListBuffer.empty[Enemy]
  }

  object needInit_pool extends GlobalPool[CanInit] {
    protected[this] var self = mutable.ListBuffer.empty[CanInit]
    def allInit() {
      self foreach CommonFunction.initFunc
      self.clear()
    }
  }

  object shipReference {

    private[this] var ship: Option[Ship] = None

    def set(s: Ship) {
      ship = Option(s)
    }

    def aim(from: Position): Double = {
      import scala.math._
      if (ship.isDefined) {
        val t = ship.get
        toDegrees( atan2(t.pos.y - from.y, t.pos.x - from.x) )
      } else 0
    }

    def x: Double = if (ship.isDefined) ship.get.pos.x else 0

    def y: Double = if (ship.isDefined) ship.get.pos.y else 0

    def reset() {
      ship = None
    }
  }

}
