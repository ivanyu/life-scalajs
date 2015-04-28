package me.ivanyu.life.logic

import me.ivanyu.life.logic.CellPlane.{CellCoords, CellState}
import scala.collection.mutable.ArrayBuffer

sealed trait CellPlaneBase {
  val width: Int
  val height: Int

  protected implicit class CellCoordsToPlaneCoords(cellCoords: CellCoords) {
    def toPlaneCoords: Int = {
      assert(cellCoords.col >= 0)
      assert(cellCoords.col < width)
      assert(cellCoords.row >= 0)
      assert(cellCoords.row < height)
      cellCoords.row * width + cellCoords.col
    }
  }
}

final class CellPlane private[logic]
    (val width: Int, val height: Int, val cells: Vector[CellState]) extends CellPlaneBase {
  import CellPlane._

  def setAlive(cellCoords: CellCoords): CellPlane = {
    val idx = cellCoords.toPlaneCoords
    copy(cells.updated(idx, Alive))
  }

  def setDead(cellCoords: CellCoords): CellPlane = {
    val idx = cellCoords.toPlaneCoords
    copy(cells.updated(idx, Dead))
  }

  def flip(cellCoords: CellCoords): CellPlane = {
    val idx = cellCoords.toPlaneCoords
    cells(idx) match {
      case Dead => copy(cells.updated(idx, Alive))
      case Alive => copy(cells.updated(idx, Dead))
      case _ => this
    }
  }

  def clear(): CellPlane = {
    CellPlane(width, height)
  }

  def get(cellCoords: CellCoords): CellState = {
    val idx = cellCoords.toPlaneCoords
    cells(idx)
  }

  private def copy(cells: Vector[CellState]): CellPlane =
    new CellPlane(width, height, cells)

  def getNeighbours(cellCoords: CellCoords): Neighbours = {
    val CellCoords(col, row) = cellCoords

    val colLeft =
      if (col > 0) col - 1
      else width - 1
    val colRight =
      if (col < width - 1) col + 1
      else 0
    val rowUpper =
      if (row > 0) row - 1
      else height - 1
    val rowLower =
      if (row < height - 1) row + 1
      else 0

    Neighbours(
      CellCoords(colLeft, rowUpper),
      CellCoords(col, rowUpper),
      CellCoords(colRight, rowUpper),
      CellCoords(colRight, row),
      CellCoords(colRight, rowLower),
      CellCoords(col, rowLower),
      CellCoords(colLeft, rowLower),
      CellCoords(colLeft, row))
  }

  override def hashCode(): Int = cells.hashCode()

  override def equals(obj: scala.Any): Boolean = {
    obj.isInstanceOf[CellPlane] && obj.asInstanceOf[CellPlane].cells.equals(this.cells)
  }
}

object CellPlane {
  sealed trait CellState
  case object Dead extends CellState
  case object Alive extends CellState
  case object Unchanged extends CellState

  case class CellCoords(row: Int, col: Int)

  case class Neighbours(upperLeft: CellCoords, upper: CellCoords,
                        upperRight: CellCoords, right: CellCoords,
                        lowerRight: CellCoords, lower: CellCoords,
                        lowerLeft: CellCoords, left: CellCoords) {
    val neighbourList = List(
      upperLeft, upper, upperRight, right,
      lowerRight, lower, lowerLeft, left)
  }

  def apply(width: Int, height: Int): CellPlane = {
    assert(width > 0)
    assert(height > 0)
    new CellPlane(width, height, Vector.fill(width * height)(Dead))
  }
}

final class CellPlaneBuffer(val width: Int, val height: Int, val filler: CellState)
    extends CellPlaneBase {
  private val cells = ArrayBuffer.fill[CellState](width * height)(filler)

  def update(cellCoords: CellCoords, state: CellState): Unit = {
    cells(cellCoords.toPlaneCoords) = state
  }

  def build(): CellPlane = new CellPlane(width, height, cells.toVector)
}

object CellPlaneBuffer {
  def apply(width: Int, height: Int, filler: CellState): CellPlaneBuffer = {
    new CellPlaneBuffer(width, height, filler)
  }
}
