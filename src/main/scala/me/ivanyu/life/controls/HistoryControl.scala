package me.ivanyu.life.controls

import me.ivanyu.life.LifeJSApp.UniverseWithEpoch
import me.ivanyu.life.controls.VolumeControl.VolumeState
import org.scalajs.dom
import org.scalajs.jquery.{JQueryEventObject, jQuery}
import rx._

import scala.scalajs.js

class HistoryControl(rootElement: dom.html.Div,
                     val historyOfTime: Var[List[UniverseWithEpoch]],
                     val running: Var[Boolean],
                     volume: Var[VolumeState])
                    (implicit ownerCtx: Ctx.Owner, dataCtx: Ctx.Data) {
  private val jqRoot = jQuery(rootElement)
  val jqSlider = new JQueryUISlider(jqRoot.children("#slider-history"))
  jqSlider.init(1, 0, 0, animate = true, disabled = true)
  private val jqSpanTimeLength = jqRoot.find("#span-time-length")

  private val audioTime = jqRoot.children("#audio-time")(0).asInstanceOf[dom.html.Audio]
  audioTime.volume = 0.2

  volume.trigger {
    audioTime.muted = volume().muted
    audioTime.volume = volume().volume
  }

  val selectionStream = Var[Int](jqSlider.sliderValue)

  historyOfTime.trigger {
    val lastEpoch = historyOfTime().headOption.map(_.epoch).getOrElse(0)
    jqSlider.max = lastEpoch
    jqSlider.sliderValue = lastEpoch

    jqSpanTimeLength.text(lastEpoch.toString)
  }

  private val disabled = Rx { running()/* || historyOfTime().isEmpty*/ }
  disabled.triggerLater {
    jqSlider.disabled = disabled()
  }

  jqSlider.onSlide((e: JQueryEventObject, ui: js.Dynamic) => {
    val sliderValue = ui.value.asInstanceOf[Int]
    audioTime.play()
    selectionStream() = sliderValue
    jqSpanTimeLength.text(sliderValue.toString)
    true.asInstanceOf[js.Any]
  })
}
