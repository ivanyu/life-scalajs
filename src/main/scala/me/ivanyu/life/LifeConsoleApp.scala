package me.ivanyu.life

import me.ivanyu.life.logic.{Universe, CellPlane}
import CellPlane.{Alive, Dead}


object LifeConsoleApp extends App {
  def printUniverse(universe: Universe): Unit = {
    for { y <- Range(0, universe.height) } {
      val lineStr = Range(0, universe.width).map { x =>
        (universe.get(x, y): @unchecked) match {
          case Dead => 'o'
          case Alive => 'x'
        }
      }.mkString(" ")
      println(lineStr)
    }
  }

  val universe1 = Universe(10, 10)
    .setAlive(1, 0)
    .setAlive(2, 1)
    .setAlive(0, 2)
    .setAlive(1, 2)
    .setAlive(2, 2)


  var currentUniverse = universe1
  for (i <- Range(0, 41)) {
    printUniverse(currentUniverse)
    println()
    currentUniverse = currentUniverse.next._1
  }
}
