package neutrino

import scala.io.Source

trait StringLike[T](using ev: String =:= T):
  self =>
  inline def apply(inline s: String): T = ev.apply(s)
  inline def raw(inline t: T): String   = ev.flip.apply(t)
  def fromResources(cls: Class[?], path: String) =
    apply(
      Source
        .fromInputStream(
          cls.getResourceAsStream(path)
        )
        .getLines()
        .mkString("\n")
    )
  end fromResources

  extension (t: T) def value = self.raw(t)
end StringLike
