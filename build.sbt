import Dependencies._

ThisBuild / scalaVersion := "2.13.2"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val root = project
  .in(file("."))
  .aggregate(engine.js, engine.jvm)
  .settings(
    name := "engine",
    libraryDependencies += scalaTest % Test,
    publish := {},
    publishLocal := {}
  )

lazy val engine = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .jvmSettings(
    libraryDependencies += scalaJsStubs % "provided"
  )
