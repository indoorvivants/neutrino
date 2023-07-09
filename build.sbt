import scala.scalanative.build.Mode

Global / onChangedBuildSource := ReloadOnSourceChanges

val V = new {
  val Scala = "3.1.2"
}

lazy val exampleBackend =
  projectMatrix
    .in(file("example-backend"))
    .nativePlatform(Seq(V.Scala))
    .dependsOn(neutrino)
    .defaultAxes(VirtualAxis.scalaABIVersion(V.Scala), VirtualAxis.native)
    .settings(
      nativeConfig := {
        val conf = nativeConfig.value

        conf
          .withLinkingOptions(
            conf.linkingOptions ++ Seq(
              ((ThisBuild / baseDirectory).value / "webview" / "webview.a").toString,
              "-framework",
              "WebKit"
            )
          )
          .withEmbedResources(true)
      },
      Compile / resourceGenerators += Def.task {
        val jsLinked = (exampleJS / Compile / fastLinkJS).value
        val path =
          (exampleJS / Compile / fastLinkJS / scalaJSLinkerOutputDirectory).value

        val file = path / "main.js"

        val dest =
          (Compile / managedResourceDirectories).value.head / "app.js"

        IO.copyFile(file, dest)

        List(dest)
      }
    )

lazy val exampleFrontend =
  projectMatrix
    .in(file("example-frontend"))
    .dependsOn(neutrino)
    .jsPlatform(Seq(V.Scala))
    .defaultAxes(VirtualAxis.scalaABIVersion(V.Scala), VirtualAxis.js)
    .settings(scalaJSUseMainModuleInitializer := true)
    .settings(libraryDependencies += "com.raquo" %%% "laminar" % "0.14.2")

lazy val exampleJS = exampleFrontend.js(V.Scala)

lazy val neutrino =
  projectMatrix
    .in(file("neutrino"))
    .jsPlatform(Seq(V.Scala))
    .nativePlatform(Seq(V.Scala))
    .defaultAxes(VirtualAxis.scalaABIVersion(V.Scala))
    .settings(
      libraryDependencies += "com.lihaoyi" %%% "upickle" % "3.1.2"
    )
