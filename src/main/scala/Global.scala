package com.github.whelmaze.bulletf

import scala.collection.mutable

/**
 *  evil global
 */
object Global {

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
