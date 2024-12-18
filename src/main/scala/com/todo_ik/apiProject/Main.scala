package com.todo_ik.apiProject

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.RouteResult // Add this import
import com.todo_ik.apiProject.db.DatabaseConfig
import com.todo_ik.apiProject.repositories.TodoRepository
import com.todo_ik.apiProject.services.TodoService
import com.todo_ik.apiProject.routes.TodoRoutes
import cats.effect.{IO, IOApp, ExitCode}
import scala.concurrent.Future

object Main extends IOApp {
  // Add this import at the top of your Main object
  import org.apache.pekko.http.scaladsl.server.directives.RouteDirectives._

  override def run(args: List[String]): IO[ExitCode] = {
    // Create ActorSystem
    implicit val system = ActorSystem("TodoApiSystem") // Make system implicit
    implicit val executionContext = system.dispatcher

    def log(message: String): IO[Unit] = IO.println(s"[DEBUG] $message")

    val program = for {
      _ <- log("Starting application...")

      // Initialize database
      xa <- DatabaseConfig.transactor.allocated.map(_._1)
      _ <- log("Database transactor acquired.")

      // Initialize components
      todoRepository = new TodoRepository(xa)
      todoService = new TodoService(todoRepository)
      todoRoutes = new TodoRoutes(todoService)
      _ <- log("Services and routes initialized.")

      // Start server
      bindingFuture <- IO.fromFuture(IO(
        Http().newServerAt("localhost", 9000)
            .bind(Route.seal(todoRoutes.routes)) // Use Route.seal instead of .seal
      ))
      _ <- log(s"Server online at http://localhost:9000/")

      // Keep the application running
      _ <- IO.never
    } yield ExitCode.Success

    // Error handling and cleanup
    program.handleErrorWith { error =>
      log(s"Application failed: ${error.getMessage}") *>
          IO.pure(ExitCode.Error)
    }.guaranteeCase { outcome =>
      log(s"Shutting down with outcome: $outcome") *>
          IO.fromFuture(IO(system.terminate())).void
    }
  }
}
