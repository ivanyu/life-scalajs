package me.ivanyu.life.logic

import me.ivanyu.life.logic.CellPlane.{CellCoords, CellState}

trait CellPlaneContainer {
  val cellPlane: CellPlane

  def get(row: Int, col: Int): CellState = {
    cellPlane.get(CellCoords(col, row))
  }

  def get(cellCoords: CellCoords): CellState = {
    cellPlane.get(cellCoords)
  }
}
