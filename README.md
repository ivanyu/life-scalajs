# life-scalajs
A simple implementation of [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life) simulator with [Scala.js](http://www.scala-js.org/) and [Scala.rx](https://github.com/lihaoyi/scala.rx).

Created just for fun by [Ivan Yurchenko](https://ivanyu.me/).

[See it in action](https://ivanyu.github.io/life-scalajs).

[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.8.svg)](https://www.scala-js.org)

# Build and run

Type

```
$ sbt ~fastOptJS
```

and open [http://localhost:12345/target/scala-2.11/classes/index-dev.html](http://localhost:12345/target/scala-2.11/classes/index-dev.html) in your browser.

Use

```
$ sbt fullOptJS
```

and `target/scala-2.11/classes/index.html` for a fully optimized version.

# License
MIT, see LICENSE
