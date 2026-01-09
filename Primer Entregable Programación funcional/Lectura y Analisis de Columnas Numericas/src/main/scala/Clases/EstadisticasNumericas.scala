package Clases

import cats.effect.{IO, IOApp}
import fs2.io.file.{Files, Path}
import fs2.data.csv._

object EstadisticasNumericas extends IOApp.Simple:

  // Ruta del archivo CSV
  val path = Path("src/main/resource/data/pi_movies_Complete.csv")

  val run: IO[Unit] =

    // Leer el CSV y extraer solo valores numéricos
    val lectura: IO[List[Double]] =
      Files[IO]
        .readUtf8(path)
        .through(decodeWithoutHeaders[List[String]](';'))
        .map(fila => fila.flatMap(celda => celda.trim.toDoubleOption))
        .compile
        .toList
        .map(_.flatten)
        .map(_.filter(_.isFinite))

    // Cálculo de estadísticas básicas
    lectura.flatMap { numeros =>
      if numeros.isEmpty then
        IO.println("No hay datos numéricos válidos.")
      else
        val total = numeros.size
        val suma = numeros.sum
        val promedio = suma / total
        val maximo = numeros.max
        val minimo = numeros.min

        IO.println("======================================") >>
          IO.println("   ESTADÍSTICAS BÁSICAS") >>
          IO.println("======================================") >>
          IO.println(s"Cantidad de valores: $total") >>
          IO.println(f"Suma total: $suma%.2f") >>
          IO.println(f"Promedio: $promedio%.2f") >>
          IO.println(f"Máximo: $maximo%.2f") >>
          IO.println(f"Mínimo: $minimo%.2f")
    }



