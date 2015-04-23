package me.ivanyu.life

import me.ivanyu.life.logic.{Changes, Universe}
import org.scalajs.dom.html.Button
import rx.ops.Timer

import scala.concurrent.duration._
import scala.scalajs.js.timers.SetTimeoutHandle
import scala.scalajs.js.{JSApp, timers}
import org.scalajs.dom

import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

object LifeJSApp extends JSApp {
  import rx._

  def main(): Unit = {
    val running = Var(false)

    val size = 35
    val universe = Var(Universe(size, size)
      .setAlive(1, 0)
      .setAlive(2, 1)
      .setAlive(0, 2)
      .setAlive(1, 2)
      .setAlive(2, 2))
    val universeChangesStream = Var[Changes](null)

    val universeView = new UniverseView(
      dom.document.getElementById("universe-view").asInstanceOf[dom.html.Canvas],
      universeChangesStream, size, size)
    val intervalInput = new IntervalInput(
      dom.document.getElementById("input-interval").asInstanceOf[dom.html.Input])
    val startStopButton = new StartStopButton(
      dom.document.getElementById("btn-start-stop").asInstanceOf[dom.html.Button],
      running)
    val clearButton = new ClearButton(
      dom.document.getElementById("btn-clear").asInstanceOf[Button])

    Obs(startStopButton.clickStream, skipInitial = true) {
      running() = !running()
    }

    Obs(clearButton.clickStream, skipInitial = true) {
      universe() = universe().clear()
      universeView.drawUniverse(universe())
      running() = false
    }

    universeView.drawUniverse(universe())

    def setTimeoutOnNextUniverse(): SetTimeoutHandle = {
      timers.setTimeout(intervalInput.changesStream().millis) {
        if (running()) {
          val newState = universe().next
          universe() = newState._1
          universeChangesStream() = newState._2
          setTimeoutOnNextUniverse()
        }
      }
    }

    val timeout = setTimeoutOnNextUniverse()

    Obs(running, skipInitial = true) {
      if (running()) {
        setTimeoutOnNextUniverse()
      } else {
        timers.clearTimeout(timeout)
      }
    }

    Obs(universeView.clickStream, skipInitial = true) {
      if (!running()) {
        val (cellX, cellY) = universeView.clickStream()
        universe() = universe().flip(cellX, cellY)
        universeView.drawUniverse(universe())
      }
    }
  }
}
