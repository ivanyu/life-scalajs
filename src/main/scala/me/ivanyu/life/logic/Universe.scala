package me.ivanyu.life.logic

final class Universe private(val cellPlane: CellPlane) extends CellPlaneContainer {
  import CellPlane._

  val width = cellPlane.width
  val height = cellPlane.height

  def setAlive(x: Int, y: Int): Universe = {
    copy(cellPlane.setAlive(x, y))
  }

  def setDead(x: Int, y: Int): Universe = {
    copy(cellPlane.setDead(x, y))
  }

  def flip(x: Int, y: Int): Universe = {
    copy(cellPlane.flip(x, y))
  }

  def clear(): Universe = {
    copy(cellPlane.clear())
  }

  def next: (Universe, Changes) = {
    val buffer = CellPlaneBuffer(cellPlane.width, cellPlane.height, Dead)
    val changesBuffer = CellPlaneBuffer(cellPlane.width, cellPlane.height, Unchanged)

    for {
      x <- Range(0, cellPlane.width)
      y <- Range(0, cellPlane.height)
    } {
      val aliveNeighbours = countAliveNeighbours(x, y)
      (cellPlane.get(x, y): @unchecked) match {
        case Dead =>
          if (aliveNeighbours == 3) {
            buffer(x, y) = Alive
            changesBuffer(x, y) = Alive
          } else {
            buffer(x, y) = Dead
          }

        case Alive =>
          if (aliveNeighbours < 2 || aliveNeighbours > 3) {
            buffer(x, y) = Dead
            changesBuffer(x, y) = Dead
          } else {
            buffer(x, y) = Alive
          }
      }
    }

    (new Universe(buffer.build()), Changes(changesBuffer.build()))
  }

  private def copy(cellPlane: CellPlane): Universe = new Universe(cellPlane)

  private def countAliveNeighbours(x: Int, y: Int): Int = {
    val neighbours = cellPlane.getNeighbours(x, y)
    neighbours.neighbourList.count {
      case Coords(xN, yN) => cellPlane.get(xN, yN) == Alive
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

