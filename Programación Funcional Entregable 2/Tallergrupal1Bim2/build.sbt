ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "Tallergrupal1Bim2",
    libraryDependencies ++= Seq(

      "org.typelevel" %% "cats-effect" % "3.5.4",


      "org.gnieh" %% "fs2-data-csv" % "1.11.1",
      "org.gnieh" %% "fs2-data-csv-generic" % "1.11.1",

      "co.fs2" %% "fs2-core" % "3.12.2",
      "co.fs2" %% "fs2-io"   % "3.12.2",


      "io.circe" %% "circe-core"    % "0.14.6",
      "io.circe" %% "circe-parser" % "0.14.6",
      "io.circe" %% "circe-generic" % "0.14.6"
    )
  )
