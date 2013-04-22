package com.github.whelmaze.bulletf

sealed trait State

object Live extends State

object Lost extends State

case class ShotBy[T](target: T) extends State

