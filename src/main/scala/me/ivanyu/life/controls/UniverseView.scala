package me.ivanyu.life.controls

import me.ivanyu.life.controls.VolumeControl.VolumeState
import me.ivanyu.life.logic
import me.ivanyu.life.logic.{Changes, Universe}
import org.scalajs.dom
import org.scalajs.jquery.jQuery
import rx._

class UniverseView(rootElement: dom.html.Div,
                   running: Var[Boolean],
                   universe: Var[Universe],
                   universeChangesStream: Var[Changes],
                   zoom: Var[Double], volume: Var[VolumeState])
                  (implicit ownerCtx: Ctx.Owner, dataCtx: Ctx.Data) {
  import logic.CellPlane._

  private val GridColor = "#EBEBEB"
  private val DeadColor = "#FFFFFF"
  private val AliveColor = "#403E40"
  private val CursorColor = "#C2BDAB"
  private val CursorDownColor = "#636054"

  private val GridOffset = 0
  private val CellBorderWidth = 1
  private val CellSize = 16

  val cellPlaneClickStream = Var(CellCoords(-1, -1))

  private val lastDrewCursor: Var[Option[CellCoords]] = Var(None)

  // Elements
  private val jqRoot = jQuery(rootElement)
  private val canvas = jqRoot.children("#universe-view")(0).asInstanceOf[dom.html.Canvas]
  private val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

  private val audioClick = jqRoot.children("#audio-click")(0).asInstanceOf[dom.html.Audio]
  audioClick.volume = 0.2
  private val audioMusic = jqRoot.children("#audio-music")(0).asInstanceOf[dom.html.Audio]
  audioMusic.volume = 0.2

  volume.trigger {
    audioClick.muted = volume().muted
    audioClick.volume = volume().volume
    audioMusic.muted = volume().muted
    audioMusic.volume = volume().volume
  }

  running.trigger {
    if (running()) {
      audioMusic.play()
    } else {
      audioMusic.pause()
    }
  }

  private val jqSpanCellCoords = jqRoot.children("#span-cell-coords")
  private val jqSpanCellCoordRow = jqSpanCellCoords.children("#span-cell-coord-row")
  private val jqSpanCellCoordCol = jqSpanCellCoords.children("#span-cell-coord-col")

  private var width: Int = 0
  private var height: Int = 0

  private var leftBorder = 0
  private var rightBorder = 0
  private var topBorder = 0
  private var bottomBorder = 0

  zoom.trigger {
    init()
  }

  private def init(): Unit = {
    width = universe().width
    height = universe().height

    leftBorder = GridOffset
    rightBorder = (GridOffset + width * CellSize * zoom()).toInt
    topBorder = GridOffset
    bottomBorder = (GridOffset + height * CellSize * zoom()).toInt

    ctx.clearRect(0, 0, canvas.width, canvas.height)

    canvas.width = Math.ceil(rightBorder - leftBorder).toInt + 1
    canvas.height = Math.ceil(bottomBorder - topBorder).toInt + 1
    ctx.scale(zoom(), zoom())

    ctx.translate(0.5, 0.5)
    drawGrid()
    drawUniverse(universe())
  }

  // Initialization
  canvas.onclick = canvasOnClick _
  canvas.onmousemove = canvasOnMouseMove _
  canvas.onmouseleave = canvasOnMouseLeave _
  canvas.onmousedown = canvasOnMouseDown _
  canvas.onmouseup = canvasOnMouseUp _

  universeChangesStream.triggerLater {
    drawChanges(universeChangesStream())
  }

  universe.trigger {
    drawUniverse(universe())
  }

  private def drawUniverse(u: Universe): Unit = {
    if (u.width != width || u.height != height) {
      init()
    }

    val changesFromBigBang = Changes(u)
    drawChanges(changesFromBigBang)
  }

  private def drawChanges(changes: Changes): Unit = {
    clearLastDrewCursor()
    lastDrewCursor() = None

    for {
      row <- Range(0, height)
      col <- Range(0, width)
    } {
      changes.get(row, col) match {
        case Dead => clearCell(CellCoords(col, row))
        case Alive => drawAliveCell(CellCoords(col, row))
        case _ =>
      }
    }
  }

  lastDrewCursor.trigger {
    // Print coordinates near the cell plane
    lastDrewCursor() match {
      case Some(CellCoords(row, col)) =>
        jqSpanCellCoords.removeClass("invisible")
        jqSpanCellCoordRow.text(row.toString)
        jqSpanCellCoordCol.text(col.toString)

      case None =>
        jqSpanCellCoords.addClass("invisible")
        jqSpanCellCoordRow.text("-1")
        jqSpanCellCoordCol.text("-1")
    }
  }

  private def drawGrid(): Unit = {
    ctx.strokeStyle = GridColor
    ctx.lineWidth = CellBorderWidth

    val horizontalLineLength = width * CellSize
    val verticalLineLength = height * CellSize

    for (i <- Range(0, width + 1)) {
      ctx.moveTo(GridOffset + i * CellSize, GridOffset)
      ctx.lineTo(GridOffset + i * CellSize, GridOffset + horizontalLineLength)
    }

    for (i <- Range(0, height + 1)) {
      ctx.moveTo(GridOffset, GridOffset + i * CellSize)
      ctx.lineTo(GridOffset + verticalLineLength, GridOffset + i * CellSize)
    }

    ctx.stroke()
  }

  private def canvasOnClick(e: dom.MouseEvent): Unit = {
    clientCoordsToCellCoordsOption(e.clientX, e.clientY) match {
      case Some(cellCoords) if !running() =>
        audioClick.play()
        cellPlaneClickStream() = cellCoords
      case _ =>
    }
  }

  private def canvasOnMouseMove(e: dom.MouseEvent): Unit = {
    clientCoordsToCellCoordsOption(e.clientX, e.clientY) match {
      case Some(cellCoords) if !running() =>
        drawCursor(cellCoords, mouseDown = false)

        // Clear the previous drew cursor if it isn't the same as the current
        lastDrewCursor() match {
          case Some(lastCursorCellCoords) if lastCursorCellCoords != cellCoords =>
            clearLastDrewCursor()
          case _ =>
        }

        lastDrewCursor() = Some(cellCoords)

      case _ =>
    }
  }

  private def canvasOnMouseDown(e: dom.MouseEvent): Unit = {
    lastDrewCursor() match {
      case Some(coord) if !running() =>
        // Expand cursor circle
        drawCursor(coord, mouseDown = true)
    }
  }

  private def canvasOnMouseUp(e: dom.MouseEvent): Unit = {
    lastDrewCursor() match {
      case Some(coord) if !running() =>
        // Shrink cursor circle
        clearLastDrewCursor()
        drawCursor(coord, mouseDown = false)
      case _ =>
    }
  }

  private def canvasOnMouseLeave(e: dom.MouseEvent): Unit = {
    if (!running()) {
      clearLastDrewCursor()
    }
    lastDrewCursor() = None
  }

  private def clearLastDrewCursor(): Unit = {
    lastDrewCursor() match {
      case Some(coord) =>
        clearCell(coord)

        universe().get(coord) match {
          case Dead => clearCell(coord)
          case Alive => drawAliveCell(coord)
          case _ =>
        }

      case _ =>
    }
  }

  private def drawAliveCell(cellCoords: CellCoords): Unit = {
    val (cellCenterX, cellCenterY) = cellCoords.cellCenter
    ctx.fillStyle = AliveColor
    ctx.beginPath()
    ctx.arc(cellCenterX, cellCenterY, CellSize * 0.4, 0, 2 * Math.PI)
    ctx.fill()
  }

  private def drawCursor(cellCoords: CellCoords, mouseDown: Boolean): Unit = {
    val (cellCenterX, cellCenterY) = cellCoords.cellCenter
    ctx.fillStyle = if (mouseDown) { CursorDownColor } else { CursorColor }
    val cursorSize = if (mouseDown) { CellSize * 0.3 } else { CellSize * 0.2 }
    ctx.beginPath()
    ctx.arc(cellCenterX, cellCenterY, cursorSize, 0, 2 * Math.PI)
    ctx.fill()
  }

  private def clearCell(cellCoords: CellCoords): Unit = {
    val (cornerX, cornerY) = cellCoords.topLeftCorner
    ctx.fillStyle = DeadColor
    ctx.beginPath()
    ctx.fillRect(cornerX + 1, cornerY + 1, CellSize - 2, CellSize - 2)
    ctx.fill()
    ctx.strokeRect(cornerX, cornerY, CellSize, CellSize)
  }


  private def clientCoordsToCellCoordsOption(clientX: Double, clientY: Double): Option[CellCoords] = {
    val clientRect = canvas.getBoundingClientRect()
    val x = clientX - clientRect.left
    val y = clientY - clientRect.top
    if (x >= leftBorder && x < rightBorder && y >= topBorder && y < bottomBorder) {
      val row = Math.floor((y - topBorder)  / zoom() / CellSize).toInt
      val col = Math.floor((x - leftBorder) / zoom() / CellSize).toInt
      Some(CellCoords(row, col))
    } else {
      None
    }
  }

  private implicit class CellCoordsToPixelCoords(cellCoords: CellCoords) {
    def topLeftCorner: (Double, Double) = {
      (GridOffset + cellCoords.col * CellSize,
       GridOffset + cellCoords.row * CellSize)
    }

    def cellCenter: (Double, Double) = {
      (GridOffset + (cellCoords.col + 0.5) * CellSize,
       GridOffset + (cellCoords.row + 0.5) * CellSize)
    }
  }
}
