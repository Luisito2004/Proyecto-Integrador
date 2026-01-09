package clases

import cats.effect.{IO, IOApp}
import io.circe.generic.auto._
import io.circe.parser.decode

object ConsultaCirce extends IOApp.Simple {

  case class Genre(
                    id: Int,
                    name: String
                  )

  case class CrewRaw(
                      id: Int,
                      name: String,
                      gender: Option[Int],
                      profile_path: Option[String],
                      department: String,
                      job: String,
                      credit_id: String
                    )

  case class MovieClean(
                         id: Int,
                         title: String,
                         budget: Int,
                         revenue: Int,
                         runtime: Int
                       )

  def parseGenres(json: String): List[Genre] =
    decode[List[Genre]](json).getOrElse(List.empty)

  def parseCrew(json: String): List[CrewRaw] =
    decode[List[CrewRaw]](json).getOrElse(List.empty)

  // ===== TRANSFORMACIONES =====
  def extractUniqueGenres(genres: List[Genre]): List[Genre] =
    genres.distinctBy(_.id)

  def extractMovieGenres(movieId: Int, genres: List[Genre]): List[(Int, Int)] =
    genres.map(g => (movieId, g.id))

  def extractCrewEntities(crew: List[CrewRaw]): List[(Int, String, Int, String)] =
    crew
      .map(c =>
        (
          c.id,
          c.name,
          c.gender.getOrElse(0),
          c.profile_path.getOrElse("Sin dato")
        )
      )
      .distinctBy(_._1)

  def extractMovieCrew(
                        movieId: Int,
                        crew: List[CrewRaw]
                      ): List[(Int, Int, String, String, String)] =
    crew.map(c =>
      (movieId, c.id, c.department, c.job, c.credit_id)
    )

  // ===== LIMPIEZA =====
  def cleanInt(value: String): Int =
    value.toIntOption.getOrElse(0)

  def cleanString(value: String): String =
    Option(value).filter(_.nonEmpty).getOrElse("Sin dato")

  def cleanMovie(
                  id: String,
                  title: String,
                  budget: String,
                  revenue: String,
                  runtime: String
                ): MovieClean =
    MovieClean(
      cleanInt(id),
      cleanString(title),
      cleanInt(budget),
      cleanInt(revenue),
      cleanInt(runtime)
    )
  
  override def run: IO[Unit] = IO {

    val genresJson =
      """
        |[
        |  {"id": 18, "name": "Drama"},
        |  {"id": 80, "name": "Crime"}
        |]
        |""".stripMargin

    val crewJson =
      """
        |[
        |  {
        |    "id": 138,
        |    "name": "Quentin Tarantino",
        |    "gender": 2,
        |    "profile_path": "/tarantino.jpg",
        |    "department": "Directing",
        |    "job": "Director",
        |    "credit_id": "52fe4c8"
        |  }
        |]
        |""".stripMargin

    val genres = parseGenres(genresJson)
    val uniqueGenres = extractUniqueGenres(genres)
    val movieGenres = extractMovieGenres(1, genres)

    val crew = parseCrew(crewJson)
    val crewEntities = extractCrewEntities(crew)
    val movieCrew = extractMovieCrew(1, crew)

    val movie = cleanMovie(
      "1",
      "Pulp Fiction",
      "8000000",
      "213000000",
      "154"
    )

    println("=== GENRES ===")
    println(uniqueGenres)

    println("\n=== MOVIE_GENRE ===")
    println(movieGenres)

    println("\n=== CREW ===")
    println(crewEntities)

    println("\n=== MOVIE_CREW ===")
    println(movieCrew)

    println("\n=== MOVIE CLEAN ===")
    println(movie)
  }
}
