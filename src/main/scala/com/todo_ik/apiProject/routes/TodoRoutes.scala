package com.todo_ik.apiProject.routes

import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.model.StatusCodes
import com.todo_ik.apiProject.models.Todo
import com.todo_ik.apiProject.services.TodoService
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.todo_ik.apiProject.utils.JsonFormats._

import scala.concurrent.ExecutionContext
import cats.effect.IO
import cats.effect.unsafe.implicits.global

class TodoRoutes(todoService: TodoService)(implicit ec: ExecutionContext) {

    val routes: Route = pathPrefix("todos") {
        concat(
            pathEndOrSingleSlash {
                concat(
                    get {
                        // List all todos
                        onSuccess(todoService.listAll().unsafeToFuture()) { todos =>
                            complete(todos) // Uses the implicit List[Todo] format
                        }
                    },
                    post {
                        // Create a new todo
                        entity(as[Todo]) { todo =>
                            onSuccess(todoService.create(todo).unsafeToFuture()) {
                                case Right(id)    => complete((StatusCodes.Created, s"Todo created with ID: $id"))
                                case Left(error)  => complete((StatusCodes.BadRequest, error))
                            }
                        }
                    }
                )
            },
            path(LongNumber) { id =>
                concat(
                    get {
                        // Get a todo by ID
                        onSuccess(todoService.getById(id).unsafeToFuture()) {
                            case Right(todo)   => complete(todo)
                            case Left(error)   => complete((StatusCodes.NotFound, error))
                        }
                    },
                    put {
                        // Update a todo by ID
                        entity(as[Todo]) { todo =>
                            onSuccess(todoService.update(todo.copy(id = Some(id))).unsafeToFuture()) {
                                case Right(_)     => complete((StatusCodes.OK, "Todo updated successfully"))
                                case Left(error)  => complete((StatusCodes.BadRequest, error))
                            }
                        }
                    },
                    delete {
                        // Delete a todo by ID
                        onSuccess(todoService.delete(id).unsafeToFuture()) {
                            case Right(_)     => complete((StatusCodes.OK, "Todo deleted successfully"))
                            case Left(error)  => complete((StatusCodes.NotFound, error))
                        }
                    }
                )
            },
            path(LongNumber / "complete") { id =>
                put {
                    onSuccess(todoService.markAsCompleted(id).unsafeToFuture()) {
                        case Right(_)     => complete((StatusCodes.OK, "Todo marked as completed"))
                        case Left(error)  => complete((StatusCodes.NotFound, error))
                    }
                }
            }
        )
    }
}
