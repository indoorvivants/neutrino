package neutrino

import neutrino.webview.types.*
import neutrino.webview.functions.*
import scala.io.Source
import scala.scalanative.unsafe.Zone
import neutrino.ipc.Context

class RunningWebview(val w: webview_t, var width: Int, var height: Int, val z: Zone):
  import scalanative.unsafe.*
  import ipc.*
  import scala.util.*

  inline def registerIPC[T: upickle.default.Reader](
      inline funcName: String
  )(
      inline handler: Context[T] => Unit
  ) =
    val ptr = alloc[stored[T]](1)(using tagof[stored[T]], z)
    !ptr = (this, handler)
    val func =
      funcName match
        case s: String  => toCString(s)(using z)

    webview_bind(
      w,
      func,
      CFuncPtr3.fromScalaFunction { (seq, req, arg) =>
        import upickle.default.*

        val jsStrings = read[List[String]](fromCString(req))
        val webview =
          arg.asInstanceOf[Ptr[stored[Any]]]

        jsStrings.foreach { str =>

          val command = Try(read[T](str))

          command match
            case Success(command) =>
              val (w, handler) = !webview
              val promise =
                (a: CString) => webview_return(w.w, seq, 0, a)

              handler(Context(command, w, promise))
            case Failure(ex) => println(ex)
          end match
        }

      },
      ptr.asInstanceOf[Ptr[Byte]]
    )
  end registerIPC
end RunningWebview
