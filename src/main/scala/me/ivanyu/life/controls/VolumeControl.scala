package me.ivanyu.life.controls

import me.ivanyu.life.controls.VolumeControl.VolumeState
import org.scalajs.dom
import org.scalajs.jquery.{JQueryEventObject, jQuery}
import rx.core.Var

import scala.scalajs.js

class VolumeControl(rootElement: dom.html.Div) {
  private val jqRoot = jQuery(rootElement)
  private val jqSlider = new JQueryUISlider(jqRoot.children("#slider-volume"))
  jqSlider.init(0, 100, 20)

  private var muted = false

  val changesStream: Var[VolumeState] =
    Var(VolumeState(volumeValue(jqSlider.sliderValue), muted = muted))

  val jqSpanVolume = jQuery(jqRoot.children("#span-volume"))
  val jqSpanMute = jQuery(jqRoot.children("#span-mute"))

  jqSlider.onSlide((e: JQueryEventObject, ui: js.Dynamic) => {
    val sliderValue = ui.value.asInstanceOf[Int]
    jqSpanVolume.text(sliderValue.toString)
    changesStream() = VolumeState(volumeValue(sliderValue), muted = muted)
    true.asInstanceOf[js.Any]
  })

  jqSpanMute.click((e: JQueryEventObject) => {
    muted = !muted

    jqSlider.disabled = muted

    if (muted) {
      jqSpanMute.removeClass("glyphicon-volume-up").addClass("glyphicon-volume-off")
      jqSpanVolume.addClass("text-muted")
    } else {
      jqSpanMute.removeClass("glyphicon-volume-off").addClass("glyphicon-volume-up")
      jqSpanVolume.removeClass("text-muted")
    }

    changesStream() = VolumeState(volumeValue(jqSlider.sliderValue), muted = muted)
  })

  private def volumeValue(sliderValue: Int): Double = sliderValue / 100.0
}

object VolumeControl {
  case class VolumeState(volume: Double, muted: Boolean)
}