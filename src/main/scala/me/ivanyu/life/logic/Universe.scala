package me.ivanyu.life.logic

final class Universe private(val cellPlane: CellPlane) extends CellPlaneContainer {
  import CellPlane._

  val width = cellPlane.width
  val height = cellPlane.height

  def setAlive(col: Int, row: Int): Universe = {
    copy(cellPlane.setAlive(CellCoords(col, row)))
  }
  def setAlive(cellCoords: CellCoords): Universe = {
    copy(cellPlane.setAlive(cellCoords))
  }

  def setDead(col: Int, row: Int): Universe = {
    copy(cellPlane.setDead(CellCoords(col, row)))
  }
  def setDead(cellCoords: CellCoords): Universe = {
    copy(cellPlane.setDead(cellCoords))
  }

  def flip(col: Int, row: Int): Universe = {
    copy(cellPlane.flip(CellCoords(col, row)))
  }
  def flip(cellCoords: CellCoords): Universe = {
    copy(cellPlane.flip(cellCoords))
  }

  def clear(): Universe = {
    copy(cellPlane.clear())
  }

  def nextState: (Universe, Changes) = {
    val buffer = CellPlaneBuffer(cellPlane.width, cellPlane.height, Dead)
    val changesBuffer = CellPlaneBuffer(cellPlane.width, cellPlane.height, Unchanged)

    for {
      row <- Range(0, cellPlane.height)
      col <- Range(0, cellPlane.width)
    } {
      val cellCoords = CellCoords(col, row)
      val aliveNeighbours = countAliveNeighbours(cellCoords)
      (cellPlane.get(cellCoords): @unchecked) match {
        case Dead =>
          if (aliveNeighbours == 3) {
            buffer(cellCoords) = Alive
            changesBuffer(cellCoords) = Alive
          } else {
            buffer(cellCoords) = Dead
          }

        case Alive =>
          if (aliveNeighbours < 2 || aliveNeighbours > 3) {
            buffer(cellCoords) = Dead
            changesBuffer(cellCoords) = Dead
          } else {
            buffer(cellCoords) = Alive
          }
      }
    }

    (new Universe(buffer.build()), Changes(changesBuffer.build()))
  }

  private def copy(cellPlane: CellPlane): Universe = new Universe(cellPlane)

  private def countAliveNeighbours(cellCoords: CellCoords): Int = {
    val neighbours = cellPlane.getNeighbours(cellCoords)
    neighbours.neighbourList.count {
      case cellCoords: CellCoords => cellPlane.get(cellCoords) == Alive
    }
  }

  override def hashCode(): Int = cellPlane.hashCode()

  override def equals(obj: scala.Any): Boolean = {
    obj.isInstanceOf[Universe] && obj.asInstanceOf[Universe].cellPlane.equals(this.cellPlane)
  }
}

object Universe {

  def apply(width: Int, height: Int): Universe = {
    assert(width > 0)
    assert(height > 0)
    new Universe(CellPlane(width, height))
  }
}

