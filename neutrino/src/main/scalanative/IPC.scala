package neutrino.ipc

import neutrino.RunningWebview

import neutrino.webview.functions.*
import neutrino.webview.types.*

import scalanative.unsafe.*

import scala.util.*

case class Context[T](cmd: T, view: RunningWebview, promise: CString => Unit)
type stored[T] = Tuple2[RunningWebview, Context[T] => Unit]
