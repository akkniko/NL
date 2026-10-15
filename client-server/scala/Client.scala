import java.io.{BufferedReader, IOException, InputStreamReader, PrintWriter}
import java.net.{InetSocketAddress, Socket}
import java.nio.charset.StandardCharsets

object Client {
  private val Host = "127.0.0.1"
  private val Port = 5053

  def main(args: Array[String]): Unit = {
    val request =
      if (args.nonEmpty) args.mkString(" ")
      else "ADD 3 5"

    val socket = new Socket()

    try {
      socket.connect(new InetSocketAddress(Host, Port), 3000)

      val reader = new BufferedReader(
        new InputStreamReader(
          socket.getInputStream,
          StandardCharsets.UTF_8
        )
      )

      val writer = new PrintWriter(
        socket.getOutputStream, true, StandardCharsets.UTF_8
      )

      writer.println(request)
      val response = reader.readLine()

      if (response == null) {
        System.err.println("Сервер закрыл соединение, не отправив ответ.")
      } else {
        println(s"Запрос: $request")
        println(s"Ответ сервера: $response")
      }
    } catch {
      case e: IOException =>
        System.err.println(
          s"Не удалось связаться с сервером $Host:$Port: ${e.getMessage}"
        )
    } finally {
      try socket.close()
      catch {
        case e: IOException =>
          System.err.println(s"Не удалось закрыть соединение: ${e.getMessage}")
      }
    }
  }
}