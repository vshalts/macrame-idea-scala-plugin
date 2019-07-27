import org.jetbrains.sbtidea.Keys._

lazy val root = project.in(file(".")).settings(
  scalaVersion  := "2.12.8",
  version       := "1.3",

  name := "macrame-idea-scala-plugin",

  ideaPluginName := "Macrame",

  ideaBuild in ThisBuild := "192.5728.98",

  ideaPluginDirectory in ThisBuild := baseDirectory.value / "idea",

  ideaExternalPlugins in ThisProject +=
    IdeaPlugin.Zip("scala-plugin", url("https://plugins.jetbrains.com/files/1347/65894/scala-intellij-bin-2019.2.14.zip")),

  ideaInternalPlugins := Seq("java", "scala-plugin")
).enablePlugins(SbtIdeaPlugin)


crossPaths := false
