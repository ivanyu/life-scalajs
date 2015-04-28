package me.ivanyu.life

import org.scalajs.dom
import org.scalajs.jquery.{JQueryEventObject, jQuery}
import rx.core.Var

import scala.scalajs.js

class GameSpeedControl(rootElement: dom.html.Div) {
  private val jqRoot = jQuery(rootElement)
  val jqSlider = new JQueryUISlider(jqRoot.children("#slider-game-speed"))
  jqSlider.init(1, 8, 6)
  val jqSpanGameSpeed = jqRoot.children("#span-game-speed")

  val changesStream = Var(jqSlider.sliderValue)

  jqSlider.onSlide((e: JQueryEventObject, ui: js.Dynamic) => {
    val speed = ui.value.asInstanceOf[Int]
    jqSpanGameSpeed.text(speed.toString)
    changesStream() = speed
    true.asInstanceOf[js.Any]
  })
}
