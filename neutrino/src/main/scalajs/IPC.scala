package neutrino.ipc

import scalajs.js.annotation.*
import scalajs.js
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.Promise
import scala.scalajs.js.JSON

object IPC:
  object window extends IPCLike:
    @js.native
    @JSGlobal("api_window")
    private def rawIPC(message: String): Promise[js.Any] = js.native

    def ipc(message: String) = rawIPC(message)

trait IPCLike:
  def ipc(message: String): Promise[js.Any]

  def promise[X, T <: Outputs[X]](cmd: T)(using
      upickle.default.Writer[T],
      upickle.default.Reader[X]
  ): Promise[X] =
    import upickle.default.*
    ipc(write(cmd)).`then` { s =>
      read[X](JSON.stringify(s))
    }
  end promise

  def future[X, T <: Outputs[X]](cmd: T)(using
      upickle.default.Writer[T],
      upickle.default.Reader[X]
  ): Future[X] =
    import upickle.default.*
    ipc(write(cmd)).toFuture.map { s =>
      read[X](JSON.stringify(s))
    }
  end future
end IPCLike
