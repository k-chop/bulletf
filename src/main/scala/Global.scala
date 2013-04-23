package com.github.whelmaze.bulletf

import scala.collection.mutable
import com.github.whelmaze.bulletf.ui._

/**
 *  evil global
 */
object Global {

  object scoreboard {
    private[this] var self: ScoreBoard = NullScoreBoard

    def set(s: ScoreBoard) {
      self = (if (s == null) NullScoreBoard else s)
    }

    def get = self.get

    def add(i: Int) {
      self.add(i)
    }

    def reset() {
      self = NullScoreBoard
    }

  }

  object enemy_pool {

    private[this] var self = mutable.ListBuffer.empty[Enemy]

    def set(e: Enemy) {
      self += e
    }

    def set(e: Enemy*) {
      self ++= e
    }

    def clear() {
      self.clear()
    }

    def fetch(): List[Enemy] = {
      val ret = self.toList
      clear()
      ret
    }

    def nonEmpty: Boolean = self.nonEmpty

  }

}
