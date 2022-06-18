package neutrino.api

import neutrino.ipc.Context
import neutrino.ipc.Outputs
import neutrino.webview.functions.*
import neutrino.webview.types.*
import neutrino.zone
import upickle.default.*
import upickle.default.write

import scala.scalanative.unsafe.*

object WindowApi:
  given ReadWriter[Window] = macroRW[Window]

  import neutrino.api.Window.*

  val handler: Context[Window] => Unit = {
    case Context(cmd, running, promise) =>
      val view = running.w
      import running.{width, height}

      given Zone = running.z

      val reSizeWindow = () => webview_set_size(view, width, height, 0)

      cmd match
        case SetTitle(newValue) =>
          webview_set_title(view, toCString(newValue))
          promise(toCString(write(true)))

        case ResizeWindow(newWidth, newHeight) =>
          width = newWidth
          height = newHeight
          webview_set_size(view, width, height, 0)
          promise(toCString(write(true)))

        case GetWidth =>
          promise(toCString(width.toString))

        case GetHeight =>
          promise(toCString(height.toString))

        case SetWidth(i) =>
          width = i
          reSizeWindow()

        case SetHeight(i) =>
          height = i
          reSizeWindow()
      end match
  }
end WindowApi
