import java.io.{BufferedReader, IOException, InputStreamReader, PrintWriter}
import java.net.{InetAddress, InetSocketAddress, ServerSocket}
import java.nio.charset.StandardCharsets

object Server {
  private val Host = "127.0.0.1"
  private val Port = 5053

  private def calculate(request: String): String = {
    if (request == null || request.trim.isEmpty) {
      "ERROR: пустой запрос"
    } else {
      val parts = request.trim.split("\\s+")

      if (parts.length != 3) {
        "ERROR: ожидается команда ADD <a> <b>"
      } else if (parts(0) != "ADD") {
        "ERROR: поддерживается только команда ADD"
      } else {
        try {
          val a = parts(1).toLong
          val b = parts(2).toLong
          java.lang.Math.addExact(a, b).toString
        } catch {
          case _: NumberFormatException =>
            "ERROR: a и b должны быть целыми числами"

          case _: ArithmeticException =>
            "ERROR: переполнение типа Long"
        }
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val server = new ServerSocket()

    try {
      server.setReuseAddress(true)
      server.bind(
        new InetSocketAddress(InetAddress.getByName(Host), Port)
      )

      println(s"Сервер запущен на $Host:$Port; ожидается один клиент...")

      val socket = server.accept()

      try {
        val reader = new BufferedReader(
          new InputStreamReader(
            socket.getInputStream,
            StandardCharsets.UTF_8
          )
        )

        val writer = new PrintWriter(
          socket.getOutputStream, true, StandardCharsets.UTF_8
        )

        val request = reader.readLine()
        val response = calculate(request)

        writer.println(response)

        println(s"Запрос: ${Option(request).getOrElse("<нет данных>")}")
        println(s"Ответ: $response")
      } finally {
        socket.close()
      }
    } catch {
      case e: IOException =>
        System.err.println(s"Сетевая ошибка: ${e.getMessage}")
    } finally {
      try server.close()
      catch {
        case e: IOException =>
          System.err.println(
            s"Не удалось закрыть серверный сокет: ${e.getMessage}"
          )
      }
    }
  }
}