package neutrino

enum Api:
  case Window

  inline def funcName =
    this match
      case Window => "api_window"
