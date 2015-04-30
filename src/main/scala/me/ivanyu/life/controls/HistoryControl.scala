package me.ivanyu.life.controls

import me.ivanyu.life.LifeJSApp.UniverseWithEpoch
import org.scalajs.dom
import org.scalajs.jquery.{JQueryEventObject, jQuery}
import rx.Var
import rx.core.{Obs, Rx}

import scala.scalajs.js

class HistoryControl(rootElement: dom.html.Div,
                     val historyOfTime: Var[List[UniverseWithEpoch]],
                     val running: Var[Boolean]) {
  private val jqRoot = jQuery(rootElement)
  val jqSlider = new JQueryUISlider(jqRoot.children("#slider-history"))
  jqSlider.init(1, 0, 0, disabled = true)
  private val jqSpanTimeLength = jqRoot.find("#span-time-length")

  val selectionStream = Var[Int](jqSlider.sliderValue)

  Obs(historyOfTime) {
    val lastEpoch = historyOfTime().headOption.map(_.epoch).getOrElse(0)
    jqSlider.max = lastEpoch
    jqSlider.sliderValue = lastEpoch

    jqSpanTimeLength.text(lastEpoch.toString)
  }

  private val disabled = Rx { running() || historyOfTime().isEmpty }
  Obs(disabled, skipInitial = true) {
    jqSlider.disabled = disabled()
  }

  jqSlider.onSlide((e: JQueryEventObject, ui: js.Dynamic) => {
    val sliderValue = ui.value.asInstanceOf[Int]
    selectionStream() = sliderValue
    jqSpanTimeLength.text(sliderValue.toString)
    true.asInstanceOf[js.Any]
  })
}
