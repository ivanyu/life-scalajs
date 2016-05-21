enablePlugins(ScalaJSPlugin)

workbenchSettings

name := "life-scalajs"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scala-js" %%% "scalajs-dom" % "0.9.0",
  "be.doeraene" %%% "scalajs-jquery" % "0.9.0",
  "com.lihaoyi" %%% "scalarx" % "0.2.8"
)

bootSnippet := "me.ivanyu.life.LifeJSApp().main();"

refreshBrowsers <<= refreshBrowsers.triggeredBy(fastOptJS in Compile)
