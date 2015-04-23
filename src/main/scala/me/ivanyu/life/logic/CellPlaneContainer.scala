package me.ivanyu.life.logic

import me.ivanyu.life.logic.CellPlane.CellState

trait CellPlaneContainer {
  val cellPlane: CellPlane

  def get(x: Int, y: Int): CellState = {
    cellPlane.get(x, y)
  }
}
