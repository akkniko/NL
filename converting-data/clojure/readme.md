Как запустить
Для этого задания нужен Clojure CLI - одной JDK недостаточно. Официальная документация поддерживает запуск standalone-скриптов с передачей аргументов через clojure -M. 

Из каталога 05_format_conversion/clojure выполните:
clojure -M main.clj

По умолчанию программа прочитает input.edn и создаст output.json. Можно указать другие имена:
clojure -M main.clj input.edn result.json


Пример ожидаемого содержимого JSON-файла:
[
  {"name":"Alice","age":20,"active":true},
  {"name":"Bob","age":22,"active":false},
  {"name":"Clara","age":19,"active":true}
]