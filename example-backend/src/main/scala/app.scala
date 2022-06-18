import scala.scalanative.unsafe.*
import scala.io.Source

import neutrino.webview.functions.*
import neutrino.*

@main def hello =
  val js = Source
    .fromInputStream(
      this.getClass().getResourceAsStream("app.js")
    )
    .getLines()
    .mkString("\n")

  WebviewApp.builder
    .withDebugOn
    .withHtml(
      Html(
        """
        <!DOCTYPE html>
        <html lang="en">
          <head>
            <meta charset="UTF-8" />
            <meta http-equiv="X-UA-Compatible" content="IE=edge" />
            <meta name="viewport" 
                  content="width=device-width, initial-scale=1.0" />
            <title>Document</title>
          </head>
          <body>
            <b>what</b>
            <div id = "appContainer" />
          </body>
        </html>
        """
      )
    )
    .withJavaScripts(JS(js))
    .run()
end hello
