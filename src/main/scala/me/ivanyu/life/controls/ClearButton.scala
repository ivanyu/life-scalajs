package me.ivanyu.life.controls

import org.scalajs.dom
import rx.Var

class ClearButton(val btnElement: dom.html.Button) {
  val clickStream = Var(false)

  btnElement.onclick = (e: dom.Event) => {
    clickStream() = true
  }
}
