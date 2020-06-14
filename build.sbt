ThisBuild / scalaVersion := "2.13.2"
ThisBuild / organization := "org.govariants"
ThisBuild / name := "engine"
ThisBuild / organizationName := "Go Variants"
ThisBuild / homepage := Some(url("https://github.com/govariants/engine"))
ThisBuild / licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

lazy val root = project
  .in(file("."))
  .aggregate(engine.js, engine.jvm)
  .settings(
    publishArtifact := false,
    publish := {},
    publishLocal := {}
  )

lazy val engine = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
    scalacOptions ++= Seq(
      "-deprecation",
      "-unchecked",
      "-Xlint",
      "-Xfatal-warnings"
    ),
    libraryDependencies ++= Seq(
      "org.govariants" %%% "sgfparser" % "0.1.0",
      "org.scalatest" %%% "scalatest" % "3.1.1" % Test
    )
  )
  .jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.0.0" % "provided"
  )
