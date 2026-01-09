package clases

import cats.effect.{IO, IOApp}
import io.circe.generic.auto.*
import io.circe.parser.decode

object Learn extends IOApp.Simple {
  case class Movie (
                    id: Int,
                    name: String,
                    rating: Double,
                    available: Boolean
                  )
  
  val Json =
    """
      {
        "id": 1,
        "name": "Inception",
        "rating": 8.8,
        "available": true
      }
    """
    
  def parseMovie(json: String): Movie =
    decode[Movie](json) match {
      case Right(movie) => movie
      case Left(err) => throw err
    }

  override def run: IO[Unit] = IO{
    println("--Movie--")
    println(parseMovie(Json))

  }
}
