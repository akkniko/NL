import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    val numbers = (1..100).toList()
    val chunks = numbers.chunked(25)
    val executor = Executors.newFixedThreadPool(4)

    try {
        val futures = chunks.mapIndexed { index, chunk ->
            executor.submit(Callable {
                val partialSum = chunk.sumOf { it.toLong() }

                println(
                    "Задача ${index + 1}: " +
                        "поток=${Thread.currentThread().name}, " +
                        "диапазон=${chunk.first()}..${chunk.last()}"
                )

                partialSum
            })
        }

        // Получаем результат каждой задачи.
        val partialSums = futures.map { it.get() }

        partialSums.forEachIndexed { index, sum ->
            println("Частичная сумма ${index + 1}: $sum")
        }

        println("Общая сумма чисел от 1 до 100: ${partialSums.sum()}")
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
        System.err.println("Ожидание задач было прервано.")
    } catch (e: ExecutionException) {
        System.err.println(
            "Ошибка при выполнении задачи: ${e.cause?.message ?: e.message}"
        )
    } finally {
        executor.shutdown()

        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}



// kotlinc Main.kt -include-runtime -d app.jar
// java -jar app.jar