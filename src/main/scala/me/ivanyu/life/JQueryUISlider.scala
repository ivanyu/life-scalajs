package me.ivanyu.life

import org.scalajs.jquery.{JQuery, JQueryEventObject}

import scala.scalajs.js

class JQueryUISlider(slider: JQuery) {
  private val dyn = slider.asInstanceOf[js.Dynamic]

  def init(min: Int, max: Int, value: Int, disabled: Boolean = false): Unit = {
    dyn.slider(js.Dynamic.literal(
      "min" -> min,
      "max" -> max,
      "value" -> value,
      "disabled" -> disabled
    ))
  }

  def max: Int = dyn.slider("option", "max").asInstanceOf[Int]
  def max_=(m: Int): Unit = dyn.slider("option", "max", m)

  def min: Int = dyn.slider("option", "min").asInstanceOf[Int]
  def min_=(m: Int): Unit = dyn.slider("option", "min", m)

  def sliderValue: Int = dyn.slider("option", "value").asInstanceOf[Int]
  def sliderValue_=(m: Int): Unit = dyn.slider("option", "value", m)

  def disabled: Boolean = dyn.slider("option", "disabled").asInstanceOf[Boolean]
  def disabled_=(v: Boolean): Unit = dyn.slider("option", "disabled", v)

  def onSlide(handler: js.Function2[JQueryEventObject, js.Dynamic, js.Any]): Unit =
    dyn.on("slide", handler)
}
