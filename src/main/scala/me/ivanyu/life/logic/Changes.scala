package me.ivanyu.life.logic

sealed case class Changes(cellPlane: CellPlane) extends CellPlaneContainer

object Changes {
  def apply(universe: Universe): Changes = {
    Changes(universe.cellPlane)
  }
}