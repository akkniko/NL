import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets

final String HOST = '127.0.0.1'
final int PORT = 5054

ServerSocket server = new ServerSocket()
Socket client = null

try {
    server.setReuseAddress(true)
    server.bind(new InetSocketAddress(InetAddress.getByName(HOST), PORT))

    println "Сервер запущен на ${HOST}:${PORT}; ожидается один клиент..."

    client = server.accept()

    BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            client.getInputStream(),
            StandardCharsets.UTF_8
        )
    )

    PrintWriter writer = new PrintWriter(
        client.getOutputStream(), true, StandardCharsets.UTF_8
    )

    String message = reader.readLine()
    String response

    if (message == null) {
        response = 'ERROR: запрос не получен'
    } else {
        // Считаем символы Unicode, а не байты UTF-8.
        int length = message.codePointCount(0, message.length())
        response = "LENGTH ${length}"
    }

    writer.println(response)

    println "Получено: ${message == null ? '<нет данных>' : message}"
    println "Отправлено: ${response}"
} catch (IOException e) {
    System.err.println "Сетевая ошибка: ${e.message}"
} finally {
    if (client != null) {
        try {
            client.close()
        } catch (IOException e) {
            System.err.println "Не удалось закрыть клиентский сокет: ${e.message}"
        }
    }

    try {
        server.close()
    } catch (IOException e) {
        System.err.println "Не удалось закрыть серверный сокет: ${e.message}"
    }
}