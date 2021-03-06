package me.ivanyu.life

import me.ivanyu.life.controls.NewUniverseModal.NewUniverseParams
import me.ivanyu.life.controls._
import me.ivanyu.life.logic.{Changes, Universe}
import org.scalajs.dom
import org.scalajs.dom.html.{Button, Div}

import scala.concurrent.duration._
import scala.scalajs.js.timers.SetTimeoutHandle
import scala.scalajs.js.{JSApp, timers}

object LifeJSApp extends JSApp {
  import rx._

  private[life] case class UniverseWithEpoch(universe: Universe, epoch: Int)

  def main(): Unit = {
    val running = Var(false)

    val universeSize = Var(35)
    val universe = Var(Universe(universeSize(), universeSize())
      // Left glider
      .setAlive(0, 1)
      .setAlive(1, 2)
      .setAlive(2, 0)
      .setAlive(2, 1)
      .setAlive(2, 2)

      // Right glider
      .setAlive(0, 32)
      .setAlive(1, 31)
      .setAlive(2, 33)
      .setAlive(2, 32)
      .setAlive(2, 31)
    )

    val currentEpoch = Var(1)
    val universeChangesStream = Var[Changes](null)

    val historyOfTime = Var {
      List(UniverseWithEpoch(universe(), currentEpoch()))
    }

    // Controls
    val clearButton = new controls.Button(
      dom.document.getElementById("btn-clear").asInstanceOf[Button])
    val randomSeedButton = new controls.Button(
      dom.document.getElementById("btn-random").asInstanceOf[Button])

    val newUniverseModal = new NewUniverseModal(
      dom.document.getElementById("modal-new-universe").asInstanceOf[Div],
      universeSize)

    val startStopButton = new StartStopButton(
      dom.document.getElementById("btn-start-stop").asInstanceOf[Button],
      running)

    val gameSpeedControl = new GameSpeedControl(
      dom.document.getElementById("control-game-speed").asInstanceOf[Div])

    val zoomControl = new ZoomControl(
      dom.document.getElementById("control-zoom").asInstanceOf[Div])

    val volumeControl = new VolumeControl(
      dom.document.getElementById("control-volume").asInstanceOf[Div])

    val universeView = new UniverseView(
      dom.document.getElementById("universe").asInstanceOf[Div],
      running, universe, universeChangesStream,
      zoomControl.zoom, volumeControl.volume)

    val historyControl = new HistoryControl(
      dom.document.getElementById("control-history").asInstanceOf[Div],
      historyOfTime, running, volumeControl.volume)


    def setTimeoutOnNextUniverse(): SetTimeoutHandle = {
      val interval = speedToDuration(gameSpeedControl.changesStream())
      timers.setTimeout(interval) {
        if (running()) {
          val newState = universe().nextState
          universe() = newState._1
          universeChangesStream() = newState._2

          val newEpoch = historyOfTime().headOption.map(_.epoch).getOrElse(0) + 1
          currentEpoch() = newEpoch
          historyOfTime() = UniverseWithEpoch(newState._1, newEpoch) :: historyOfTime()
          setTimeoutOnNextUniverse()
        }
      }
    }

    val timeout = setTimeoutOnNextUniverse()

    // On "Clear" button, drop the history and clear the universe
    Obs(clearButton.clickStream, skipInitial = true) {
      startNewHistory(universe().clear())
    }

    // On "Random seed" button, drop the history and generate new random universe
    Obs(randomSeedButton.clickStream, skipInitial = true) {
      startNewHistory(Universe.random(universeSize(), universeSize()))
    }

    Obs(newUniverseModal.newUniverseParamsStream, skipInitial = true) {
      val NewUniverseParams(size, randomSeed) = newUniverseModal.newUniverseParamsStream()

      universeSize() = size
      val newUniverse =
        if (randomSeed) {
          Universe.random(size, size)
        } else {
          Universe(size, size)
        }
      startNewHistory(newUniverse)
    }

    Obs(startStopButton.clickStream, skipInitial = true) {
      running() = !running()
    }

    def startNewHistory(u: Universe): Unit = {
      running() = false
      universe() = u
      currentEpoch() = 1
      historyOfTime() = List(UniverseWithEpoch(u, currentEpoch()))
    }

    // On clicking in cell plane if running, drop the history and modify the universe
    Obs(universeView.cellPlaneClickStream, skipInitial = true) {
      if (!running()) {
        universe() = universe().flip(universeView.cellPlaneClickStream())
        currentEpoch() = 1
        historyOfTime() = List(UniverseWithEpoch(universe(), currentEpoch()))
      }
    }

    Obs(running, skipInitial = true) {
      if (running()) {
        // On start running, drop all the history after currently selected epoch
        // (in case we got back in time, the future will be dropped;
        // in case of simple pause, nothing will be dropped).
        val lastHistoryEpoch = historyOfTime().head.epoch
        val toDrop = lastHistoryEpoch - currentEpoch()
        historyOfTime() = historyOfTime().drop(toDrop)
        setTimeoutOnNextUniverse()
      } else {
        timers.clearTimeout(timeout)
      }
    }

    // Moving through time: setting current universe state and epoch
    Obs(historyControl.selectionStream, skipInitial = true) {
      if (!running()) {
        val UniverseWithEpoch(u, e) = historyOfTime()
          .drop(historyOfTime().size - historyControl.selectionStream()).head
        universe() = u
        currentEpoch() = e
      }
    }
  }

  private def speedToDuration(speed: Int): FiniteDuration = {
    speed match {
      case 8 => 10.millis
      case 7 => 50.millis
      case 6 => 100.millis
      case 5 => 200.millis
      case 4 => 300.millis
      case 3 => 500.millis
      case 2 => 700.millis
      case 1 => 1000.millis
    }
  }
}
