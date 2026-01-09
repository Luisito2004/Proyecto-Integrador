package clases

import cats.effect.{IO, IOApp}
import fs2.Stream
import fs2.io.file.{Files, Path}
import fs2.data.csv._

object ColumnasTextoFrecuencia extends IOApp.Simple {

  val path: Path =
    Path("src/main/resources/movies/pi_movies_complete.csv")

  val indexOriginalLanguage: Int = 7
  def esIdiomaValido(s:String):Boolean =
    Set("en", "es", "fr", "de", "it", "pt", "ru", "ja", "zh", "ko", "ar", "hi").contains(s)

  val indexStatus: Int = 18
  def esEstadoValido(s:String):Boolean =
    Set("released", "rumored", "post pro", "in produ").contains(s)


  def normalizarTexto(valor: String): String =
    valor.trim.toLowerCase

  def contarFrecuencias(valores: List[String]): Map[String, Long] =
    valores.groupBy(identity).view.mapValues(_.size.toLong).toMap

  val run: IO[Unit] =
    Files[IO]
      .readUtf8(path)
      .through(decodeWithoutHeaders[List[String]](';'))
      .drop(1)
      .map { fila =>
        val idioma: String =
          if (fila.isDefinedAt(indexOriginalLanguage))
            normalizarTexto(fila(indexOriginalLanguage))
          else
            "desconocido"

        val estado: String =
          if (fila.isDefinedAt(indexStatus))
            normalizarTexto(fila(indexStatus))
          else
            "desconocido"

        List(
          Option.when(esIdiomaValido(idioma))(idioma),
          Option.when(esEstadoValido(estado))(estado)
        ).flatten
      }
      .flatMap(Stream.emits)
      .filter(valor => valor.nonEmpty && valor != "null")
      .compile
      .toList
      .flatMap { textos =>
        val frecuencias: Map[String, Long] =
          contarFrecuencias(textos)

        IO {
          println("Distribución de frecuencia (columnas tipo texto):")
          frecuencias.foreach { case (valor, cantidad) =>
            println(s"$valor -> $cantidad")
          }
        }
      }
}
