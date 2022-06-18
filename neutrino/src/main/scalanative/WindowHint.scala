package neutrino

import neutrino.webview.types.*
import neutrino.webview.functions.*
import scala.io.Source
import scala.scalanative.unsafe.Zone
import neutrino.ipc.Context

enum WindowHint:
  case Min, Max
  case Fixed
  case Default

  // Window size hints
  // #define WEBVIEW_HINT_NONE 0  // Width and height are default size
  // #define WEBVIEW_HINT_MIN 1   // Width and height are minimum bounds
  // #define WEBVIEW_HINT_MAX 2   // Width and height are maximum bounds
  // #define WEBVIEW_HINT_FIXED 3 // Window size can not be changed by a user
  def toInt =
    this match
      case Min     => 1
      case Max     => 2
      case Fixed   => 3
      case Default => 0
end WindowHint
