import org.jetbrains.sbtidea.Keys._

lazy val root = project.in(file(".")).settings(
  scalaVersion  := "2.12.8",
  version       := "1.4",

  name := "macrame-idea-scala-plugin",

  intellijPluginName := "Macrame",

  intellijBuild in ThisBuild := "192.5728.98",

  intellijDownloadDirectory in ThisBuild := baseDirectory.value / "idea",

  intellijExternalPlugins += "org.intellij.scala".toPlugin,

  intellijInternalPlugins := Seq("java")
).enablePlugins(SbtIdeaPlugin)


crossPaths := false
