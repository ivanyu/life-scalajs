package me.ivanyu.life

import org.scalajs.dom
import rx.core.Var

import scala.util.control.NonFatal

class IntervalInput(val inpElement: dom.html.Input) {
  val changesStream = Var(inpElement.value.toInt)

  inpElement.onchange = (e: dom.Event) => {
    try {
      val i = inpElement.value.toInt
      changesStream() = i
    } catch {
      case NonFatal(_) =>
    }
  }
}
