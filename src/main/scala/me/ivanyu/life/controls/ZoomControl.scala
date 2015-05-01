package me.ivanyu.life.controls

import org.scalajs.dom
import org.scalajs.jquery.{JQueryEventObject, jQuery}
import rx.core.Var

import scala.scalajs.js

class ZoomControl(rootElement: dom.html.Div) {
  private val jqRoot = jQuery(rootElement)
  private val jqSlider = new JQueryUISlider(jqRoot.children("#slider-zoom"))
  jqSlider.init(1, 7, 3, animate = true)

  private val jqSpanZoom = jqRoot.children("#span-zoom")

  val changesStream = Var(getZoom(jqSlider.sliderValue))

  jqSlider.onSlide((e: JQueryEventObject, ui: js.Dynamic) => {
    val zoom = getZoom(ui.value.asInstanceOf[Int])
    jqSpanZoom.text(String.format("%0.2f×", java.lang.Double.valueOf(zoom)))
    changesStream() = zoom
    true.asInstanceOf[js.Any]
  })

  private def getZoom(sliderValue: Int): Double = {
    sliderValue match {
      case 1 => 0.50
      case 2 => 0.75
      case 3 => 1.00
      case 4 => 1.25
      case 5 => 1.50
      case 6 => 1.75
      case 7 => 2.00
    }
  }
}
