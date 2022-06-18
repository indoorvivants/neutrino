import com.raquo.laminar.api.L.*

import org.scalajs.dom

import neutrino.ipc.*
import neutrino.api.*
import concurrent.ExecutionContext.Implicits.global

@main def hello =
  documentEvents.onDomContentLoaded.foreach { _ =>
    val appContainer =
      dom.document.querySelector("#appContainer")
    val agreed = Var(false)

    val widthVar  = Var(1024)
    val heightVar = Var(600)

    IPC.window.future(Window.GetWidth).map { v =>
      widthVar.set(v)
    }
    IPC.window.future(Window.GetHeight).map { v =>
      heightVar.set(v)
    }

    inline def but(title: String, v: Var[Int], f: Int => Int) =
      button(
        title,
        onClick --> { _ =>
          v.update(f)
        }
      )

    val appElement = div(
      h1(
        fontSize := "6rem",
        "You ",
        child <-- agreed.signal.map {
          case true  => "agree"
          case false => "disagree"
        }
      ),
      button(
        fontSize := "5rem",
        "Well ackchually",
        onClick --> { _ =>
          agreed.update(!_)
        }
      ),
      agreed.signal --> { value =>
        IPC.window.future(Window.SetTitle(s"Yeah boi $value"))
      },
      b("Width"),
      but("+", widthVar, _ + 10),
      but("-", widthVar, _ - 10),
      b("Height"),
      but("+", heightVar, _ + 10),
      but("-", heightVar, _ - 10),
      widthVar --> { v =>
        IPC.window.future(Window.SetWidth(v))
      },
      heightVar --> { v =>
        IPC.window.future(Window.SetHeight(v))
      },
      p("Current width: ", child.text <-- widthVar.signal.map(_.toString)),
      p("Current height: ", child.text <-- heightVar.signal.map(_.toString))
    )
    render(appContainer, appElement)
  }(unsafeWindowOwner)
