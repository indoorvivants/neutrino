package neutrino

import neutrino.webview.types.*
import neutrino.webview.functions.*

import scala.io.Source
import scala.scalanative.unsafe.Zone
import neutrino.ipc.Context

def zone[A](f: Zone ?=> A) = Zone { z => f(using z) }

class WebviewApp private (builder: AppConfig):
  private def copy(f: AppConfig => AppConfig) = new WebviewApp(f(builder))
  def withDebugOn                             = copy(_.copy(debug = true))
  def withDebugOff                            = copy(_.copy(debug = false))

  def withTitle(title: String) = copy(_.copy(title = Some(title)))
  def withApisEnabled(api: Api*) =
    copy(b => b.copy(apis = b.apis.toSet ++ api.toSet))

  def withJavaScripts(js: JS*) =
    copy(b => b.copy(js = b.js.toList ++ js.toList))

  def withHtml(html: Html) =
    copy(_.copy(html = Some(html)))

  def withWindowSize(size: WindowSize) = copy(_.copy(windowSize = size))

  def withIPC[T: upickle.default.Reader](
      funcName: String
  )(
      handler: Context[T] => Unit
  ) =
    copy { b =>
      val current = b.ipcs
      val f = (z: Zone) ?=>
        (w: RunningWebview) =>
          // TODO
          w.registerIPC(funcName)(handler)

      b.copy(ipcs = current :+ f)
    }

  def run() =
    Zone { implicit z =>
      import scala.scalanative.unsafe.*

      import builder.*
      val w = webview_create(if debug then 1 else 0, null)
      title.foreach { t =>
        webview_set_title(w, toCString(t))
      }

      html.foreach { h =>
        webview_set_html(w, toCString(h.value))
      }

      js.foreach { j =>
        webview_init(w, toCString(j.value))
      }

      val running = RunningWebview(w, windowSize.width, windowSize.height, z)

      ipcs.foreach { f =>
        f(running)
      }

      apis.foreach { api =>
        val funcName = api.funcName
        val handler = api match
          case Api.Window =>
            running.registerIPC(funcName)(neutrino.api.WindowApi.handler)
      }

      webview_set_size(w, windowSize.width, windowSize.height, windowHint.toInt)

      webview_run(w)
      webview_destroy(w)
    }

end WebviewApp

object WebviewApp:
  def builder = new WebviewApp(AppConfig())
