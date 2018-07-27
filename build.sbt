name := "macrame-idea-scala-plugin"
version := "1.0"
scalaVersion := "2.12.6"
ideaExternalPlugins += IdeaPlugin.Id("Scala", "org.intellij.scala", Some("eap"))
ideaBuild := "181.5281.24"
crossPaths := false

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
aggregate in updateIdea := false
assemblyExcludedJars in assembly := ideaFullJars.value
