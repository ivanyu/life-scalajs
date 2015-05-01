package me.ivanyu.life.controls

import me.ivanyu.life.controls.NewUniverseModal.NewUniverseParams
import org.scalajs.dom
import org.scalajs.jquery._
import rx._

import scala.scalajs.js

class NewUniverseModal(rootElement: dom.html.Div,
                       universeSize: Var[Int]) {
  val newUniverseParamsStream = Var(NewUniverseParams(-1, randomSeed = false))

  private val jqRoot = jQuery(rootElement)
  private val modalDyn = jqRoot.asInstanceOf[js.Dynamic]

  private val jqSlider = new JQueryUISlider(jqRoot.find("#slider-new-universe-size"))
  jqSlider.init(10, 150, 35, animate = true)

  private val jqSpanZoom = jqRoot.find("#span-new-universe-size")
  private val jqBtnCreate = jqRoot.find("#btn-create-universe")
  private val jqCbRandomSeed = jqRoot.find("#cb-new-universe-random-seed")

  jqSlider.onSlide((e: JQueryEventObject, ui: js.Dynamic) => {
    val size = ui.value.asInstanceOf[Int]
    jqSpanZoom.text(size.toString)
    true.asInstanceOf[js.Any]
  })

  jqBtnCreate.on("click", null, null, (e: JQueryEventObject) => {
    val size = jqSlider.sliderValue
    val randomSeed = jqCbRandomSeed.is(":checked")
    newUniverseParamsStream() = NewUniverseParams(size, randomSeed = randomSeed)
    modalDyn.modal("hide")
    true.asInstanceOf[js.Any]
  })

  Obs(universeSize) {
    jqSlider.sliderValue = universeSize()
  }
}

object NewUniverseModal {
  case class NewUniverseParams(size: Int, randomSeed: Boolean)
}