package com.github.whelmaze.bulletf

trait Behavior {

  def run(delta: Int)(implicit unit: ScriptedMove)
  
}
