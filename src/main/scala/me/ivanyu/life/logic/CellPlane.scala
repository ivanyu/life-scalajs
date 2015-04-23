package me.ivanyu.life.logic

import me.ivanyu.life.logic.CellPlane.CellState
import scala.collection.mutable.ArrayBuffer

sealed trait CellPlaneBase {
  val width: Int
  val height: Int

  protected def lineCoord(x: Int, y: Int): Int = {
    assert(x >= 0)
    assert(x < width)
    assert(y >= 0)
    assert(y < height)
    y * width + x
  }

  protected def planeCoords(i: Int): (Int, Int) = {
    val x = i % width
    val y = i / width
    (x, y)
  }
}

final class CellPlane private[logic]
    (val width: Int, val height: Int, val cells: Vector[CellState]) extends CellPlaneBase {
  import CellPlane._

  def setAlive(x: Int, y: Int): CellPlane = {
    val idx = lineCoord(x, y)
    copy(cells.updated(idx, Alive))
  }

  def setDead(x: Int, y: Int): CellPlane = {
    val idx = lineCoord(x, y)
    copy(cells.updated(idx, Dead))
  }

  def flip(x: Int, y: Int): CellPlane = {
    val idx = lineCoord(x, y)
    cells(idx) match {
      case Dead => copy(cells.updated(idx, Alive))
      case Alive => copy(cells.updated(idx, Dead))
      case _ => this
    }
  }

  def clear(): CellPlane = {
    CellPlane(width, height)
  }

  def get(x: Int, y: Int): CellState = {
    val idx = lineCoord(x, y)
    cells(idx)
  }

  private def copy(cells: Vector[CellState]): CellPlane =
    new CellPlane(width, height, cells)

  def getNeighbours(x: Int, y: Int): Neighbours = {
    val xLeft =
      if (x > 0) x - 1
      else width - 1
    val xRight =
      if (x < width - 1) x + 1
      else 0
    val yUpper =
      if (y > 0) y - 1
      else height - 1
    val yLower =
      if (y < height - 1) y + 1
      else 0

    Neighbours(
      Coords(xLeft, yUpper),
      Coords(x, yUpper),
      Coords(xRight, yUpper),
      Coords(xRight, y),
      Coords(xRight, yLower),
      Coords(x, yLower),
      Coords(xLeft, yLower),
      Coords(xLeft, y))
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

  case class Coords(x: Int, y: Int)

  case class Neighbours(upperLeft: Coords, upper: Coords,
                        upperRight: Coords, right: Coords,
                        lowerRight: Coords, lower: Coords,
                        lowerLeft: Coords, left: Coords) {
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

  def update(x: Int, y: Int, state: CellState): Unit = {
    cells(lineCoord(x, y)) = state
  }

  def build(): CellPlane = new CellPlane(width, height, cells.toVector)
}

object CellPlaneBuffer {
  def apply(width: Int, height: Int, filler: CellState): CellPlaneBuffer = {
    new CellPlaneBuffer(width, height, filler)
  }
}
