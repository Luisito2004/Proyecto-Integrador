import scala.collection.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "Archivos CSV",
    libraryDependencies ++= Seq(
      "org.gnieh" %% "fs2-data-csv" % "1.11.1",
      "org.gnieh" %% "fs2-data-csv-generic" % "1.11.1", // Para derivación automática
      "co.fs2" %% "fs2-core" % "3.12.2",
      "co.fs2" %% "fs2-io" % "3.12.2"
    )
  )
