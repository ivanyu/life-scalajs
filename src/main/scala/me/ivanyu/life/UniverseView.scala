package me.ivanyu.life

import me.ivanyu.life.logic.{Changes, Universe}
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import rx.Var
import rx.core.Obs

class UniverseView(val canvas: Canvas,
                   val universeChangesStream: Var[Changes],
                   val width: Int, val height: Int) {
  import logic.CellPlane._

  canvas.onclick = canvasOnClick _
  val clickStream = Var((0, 0))

  private val context = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  private val gridOffset = 0
  private val cellBorderWidth = 1
  private val cellSize = 16

  private val leftBorder = gridOffset
  private val rightBorder = gridOffset + width * cellSize
  private val topBorder = gridOffset
  private val bottomBorder = gridOffset + height * cellSize

  // Initialization
  canvas.width = rightBorder - leftBorder + 1
  canvas.height = bottomBorder - topBorder + 1
  context.translate(0.5, 0.5)
  drawGrid(width, height)

  Obs(universeChangesStream, skipInitial = true) {
    drawChanges(universeChangesStream())
  }

  def drawUniverse(universe: Universe): Unit = {
    val changesFromBigBang = Changes(universe)
    drawChanges(changesFromBigBang)
  }

  def drawChanges(changes: Changes): Unit = {
    for (y <- Range(0, height)) {
      for (x <- Range(0, width)) {
        val cellStartX = gridOffset + x * cellSize
        val cellStartY = gridOffset + y * cellSize
        val cellCenterX = gridOffset + (x + 0.5) * cellSize
        val cellCenterY = gridOffset + (y + 0.5) * cellSize

        (changes.get(x, y): @unchecked) match {
          case Dead =>
            context.fillStyle = "#FFFFFF"
            context.beginPath()
            context.fillRect(cellStartX + 1, cellStartY + 1, cellSize - 2, cellSize - 2)
            context.fill()
            context.strokeRect(cellStartX, cellStartY, cellSize, cellSize)

          case Alive =>
            context.fillStyle = "#403E40"
            context.beginPath()
            context.arc(cellCenterX, cellCenterY, cellSize * 0.4, 0, 2 * Math.PI)
            context.fill()

          case _ =>
        }
      }
    }
  }

  private def drawGrid(width: Int, height: Int): Unit = {
    context.strokeStyle = "#EBEBEB"

    context.lineWidth = cellBorderWidth

    val horizontalLineLength = width * cellSize
    val verticalLineLength = height * cellSize

    for (i <- Range(0, width + 1)) {
      context.moveTo(gridOffset + i * cellSize, gridOffset)
      context.lineTo(gridOffset + i * cellSize, gridOffset + horizontalLineLength)
    }

    for (i <- Range(0, height + 1)) {
      context.moveTo(gridOffset, gridOffset + i * cellSize)
      context.lineTo(gridOffset + verticalLineLength, gridOffset + i * cellSize)
    }

    context.stroke()
  }

  private def canvasOnClick(e: dom.MouseEvent): Unit = {
    val x = e.pageX - canvas.offsetLeft
    val y = e.pageY - canvas.offsetTop
    if (x >= leftBorder && x < rightBorder &&
        y >= topBorder && y < bottomBorder) {

      val cellX = Math.floor((x - leftBorder) / cellSize)
      val cellY = Math.floor((y - topBorder) / cellSize)

      clickStream() = (cellX.toInt, cellY.toInt)
    }
  }
}
