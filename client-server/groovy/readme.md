# Как запустить:
Из каталога 04_client_server/groovy откройте 2 терминала:

Первый:
groovy server.groovy

Второй:
groovy client.groovy

Groovy CLI запускает файлы .groovy непосредственно из командной строки. 


Клиент отправит Hello, server!, а сервер вернёт:
LENGTH 14

Можно отправить собственную строку:
groovy client.groovy Hello there

Ожидаемый ответ - LENGTH 11