package com.github.whelmaze.bulletf

trait Scene {

  def name(): String

  init()
  /**
    * delta is milliseconds.
   */
  def update(delta: Int): Scene
  
  def run()
  
  def draw()
  
  def init()
  
  def dispose()
  
}
