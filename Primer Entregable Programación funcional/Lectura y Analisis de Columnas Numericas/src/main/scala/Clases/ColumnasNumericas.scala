package Clases

import cats.effect.{IO, IOApp}
import fs2.io.file.{Files, Path}
import fs2.data.csv._

object ColumnasNumericas extends IOApp.Simple:

  val path = Path("src/main/resource/data/pi_movies_Complete.csv")

  val run: IO[Unit] =
    Files[IO]
      .readUtf8(path)
      .through(decodeWithoutHeaders[List[String]](';'))
      .map { fila =>
        // Nos quedamos SOLO con valores numéricos
        fila.flatMap { celda =>
          val limpia = celda.trim
          // Intenta Double primero (porque hay decimales)
          limpia.toDoubleOption
        }
      }
      .filter(_.nonEmpty) // descarta filas sin números
      .evalMap { numeros =>
        IO.println(s"Columnas numéricas encontradas: $numeros")
      }
      .compile
      .drain
