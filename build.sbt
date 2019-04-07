import sbtassembly.Plugin.AssemblyKeys._

name := "parser"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
// This statement includes the assembly plug-in capabilities
assemblySettings
// Configure JAR used with the assembly plug-in
jarName in assembly := "crime.jar"