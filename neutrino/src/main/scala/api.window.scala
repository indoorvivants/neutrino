package neutrino.api

import upickle.default.*
import neutrino.ipc.Outputs

sealed trait Window
object Window:
  case class SetTitle(value: String) extends Window, Outputs[Boolean]
  case class ResizeWindow(newWidth: Int, newHeight: Int)
      extends Window,
        Outputs[Boolean]
  case class SetWidth(newWidth: Int)   extends Window, Outputs[Boolean]
  case class SetHeight(newHeight: Int) extends Window, Outputs[Boolean]

  case object GetWidth  extends Window, Outputs[Int]
  case object GetHeight extends Window, Outputs[Int]

  given ReadWriter[SetWidth]     = macroRW[SetWidth]
  given ReadWriter[SetHeight]    = macroRW[SetHeight]
  given ReadWriter[ResizeWindow] = macroRW[ResizeWindow]
  given ReadWriter[SetTitle]     = macroRW[SetTitle]
  given ReadWriter[Window]       = macroRW[Window]

end Window
