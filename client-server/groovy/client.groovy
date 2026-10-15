import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.charset.StandardCharsets

final String HOST = '127.0.0.1'
final int PORT = 5054

String message = args.length > 0 ? args.join(' ') : 'Hello, server!'
Socket socket = new Socket()

try {
    socket.connect(new InetSocketAddress(HOST, PORT), 3000)

    BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            socket.getInputStream(),
            StandardCharsets.UTF_8
        )
    )

    PrintWriter writer = new PrintWriter(
        socket.getOutputStream(), true, StandardCharsets.UTF_8
    )

    writer.println(message)
    String response = reader.readLine()

    if (response == null) {
        System.err.println('Сервер закрыл соединение, не отправив ответ.')
    } else {
        println "Отправлено: ${message}"
        println "Ответ сервера: ${response}"
    }
} catch (IOException e) {
    System.err.println(
        "Не удалось связаться с сервером ${HOST}:${PORT}: ${e.message}"
    )
} finally {
    try {
        socket.close()
    } catch (IOException e) {
        System.err.println "Не удалось закрыть соединение: ${e.message}"
    }
}