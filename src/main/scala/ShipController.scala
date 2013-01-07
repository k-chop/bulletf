package com.github.whelmaze.bulletf

import org.lwjgl.input.Keyboard

object ShipBehaivor {
  private val sqrt2 = 1.414213
  
  def move(ship: Ship, delta: Int) = {
    import Input.keys._
    
    val plus = 0.25 * delta
    val (ix, iy) = (Input.x * plus, Input.y * plus)
    val slow = if (Input(LAZER)) 2 else 1
    val vxy: (Double, Double) = if (ix*iy != 0) (ix/sqrt2/slow, iy/sqrt2/slow) else (ix/slow, iy/slow)
    
    // if ( key(KEY_RIGHT) ) vx += plus
    // if ( key(KEY_LEFT) ) vx -= plus
    // if ( key(KEY_DOWN) ) vy += plus
    // if ( key(KEY_UP) ) vy -= plus
    // if ( key(KEY_Z) ) {}
    
    ship.pos = ship.pos.add(vxy)
  }

}
