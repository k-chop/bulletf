package com.github.whelmaze.bulletf

object STGGame {
  def main(args: Array[String]) {

    (new Game(constants.screenWidth, constants.screenHeight)).start()
    
  }
}
