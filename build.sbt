ThisBuild / scalaVersion := "2.13.2"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val commonSettings = List(
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-Xlint",
    "-Xfatal-warnings"
  )
)

lazy val root = project
  .in(file("."))
  .aggregate(engine.js, engine.jvm)
  .settings(
    name := "engine",
    commonSettings,
    publish := {},
    publishLocal := {}
  )

lazy val engine = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.1.1" % Test
  )
  .jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.0.0" % "provided"
  )
