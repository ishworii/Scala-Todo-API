package com.todo_ik.apiProject.services

import com.todo_ik.apiProject.models.{Todo, TodoStatus}
import com.todo_ik.apiProject.repositories.TodoRepository
import cats.effect.IO
import com.todo_ik.apiProject.models.TodoStatus.Completed

class TodoService(repository: TodoRepository){

    def create(todo:Todo) : IO[Either[String,Int]] = {
        if (todo.isValid){
            repository.create(todo).map(Right(_))
        }
        else
            IO.pure(Left("Invalid Todo: title must be non-empty, max 100 chars, priority between 1 and 5."))
    }

    def getById(id : Long) : IO[Either[String,Todo]] = {
        repository.findById(id).map {
            case Some(todo) => Right(todo)
            case None => Left(s"Todo with ID $id not found.")
        }
    }

    def update(todo:Todo) : IO[Either[String,Int]] = {
        if (todo.id.isEmpty){
            IO.pure(Left("Todo ID must be provided for update."))
        }
        else if(!todo.isValid){
            IO.pure(Left("Invalid Todo: title must be non-empty, max 100 chars, priority between 1 and 5."))
        }
        else{
            repository.update(todo).map { rowsUpdated =>
                if(rowsUpdated > 0) Right(rowsUpdated)
                else Left(s"Todo with ID ${todo.id} not found.")
            }
        }
    }

    def delete(id : Long) : IO[Either[String,Int]] = {
        repository.delete(id).map {rowsDeleted =>
            if (rowsDeleted > 0) Right(rowsDeleted)
            else Left(s"Todo with ID $id not found.")
        }
    }

    def listAll() : IO[List[Todo]] = {
        repository.listAll()
    }

    def markAsCompleted(id : Long) : IO[Either[String,Int]] = {
        repository.findById(id).flatMap {
            case Some(todo) =>
                val updatedTodo = todo.copy(status=Completed)
                repository.update(updatedTodo).map(Right(_))
            case None =>
                IO.pure(Left(s"Todo with ID $id not found."))
        }
    }
}