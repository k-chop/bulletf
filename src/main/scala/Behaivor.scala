package com.github.whelmaze.bulletf

trait Behaivor {

  def run(delta: Int)(implicit bullet: Bullet)
  
}
