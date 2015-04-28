package me.ivanyu.life

import org.scalajs.dom
import rx.Var
import rx.core.Obs

class StartStopButton(val btnElement: dom.html.Button,
                      val running: Var[Boolean]) {
  val clickStream = Var(false)

  val label = btnElement.getElementsByClassName("btn-label")(0).asInstanceOf[dom.html.Span]
  val symbol = btnElement.getElementsByClassName("btn-symbol")(0).asInstanceOf[dom.html.Span]

  btnElement.onclick = (e: dom.Event) => {
    clickStream() = !clickStream()
  }

  Obs(running, skipInitial = true) {
    if (running()) {
      label.textContent = "Stop"
      symbol.classList.remove("glyphicon-play")
      symbol.classList.add("glyphicon-stop")
    } else {
      label.textContent = "Start"
      symbol.classList.remove("glyphicon-stop")
      symbol.classList.add("glyphicon-play")
    }
  }
}
