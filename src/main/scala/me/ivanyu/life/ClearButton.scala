package me.ivanyu.life

import org.scalajs.dom
import rx._

class ClearButton(val btnElement: dom.html.Button) {
  val clickStream = Var(false)

  btnElement.onclick = (e: dom.Event) => {
    clickStream() = true
  }
}
