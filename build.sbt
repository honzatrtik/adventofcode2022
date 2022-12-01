val scala3Version = "3.2.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "Advent of code 2022",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.4",
      "dev.zio" %% "zio-streams" % "2.0.4",
    ),
  )
