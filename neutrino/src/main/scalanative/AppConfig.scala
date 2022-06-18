package neutrino

import neutrino.webview.types.*
import neutrino.webview.functions.*
import scala.io.Source
import scala.scalanative.unsafe.Zone
import neutrino.ipc.Context

private[neutrino] case class AppConfig(
    windowSize: WindowSize = WindowSize(640, 480),
    windowHint: WindowHint = WindowHint.Default,
    debug: Boolean = false,
    title: Option[String] = None,
    html: Option[Html] = None,
    js: List[JS] = Nil,
    apis: Set[Api] = Set(Api.Window),
    ipcs: List[Zone ?=> RunningWebview => Unit] = List.empty
)
