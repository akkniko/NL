(ns main
  (:require [clojure.edn :as edn]
            [clojure.string :as str])
  (:import [java.math BigDecimal]
           [java.io FileNotFoundException IOException]))

(defn json-string
  "Кодирует строку в JSON, включая управляющие символы."
  [s]
  (let [builder (StringBuilder.)]
    (.append builder \")
    (doseq [ch s]
      (case ch
        \" (.append builder "\\\"")
        \\ (.append builder "\\\\")
        \backspace (.append builder "\\b")
        \formfeed (.append builder "\\f")
        \newline (.append builder "\\n")
        \return (.append builder "\\r")
        \tab (.append builder "\\t")
        (if (< (int ch) 32)
          (.append builder (format "\\u%04x" (int ch)))
          (.append builder ch))))
    (.append builder \")
    (.toString builder)))

(declare to-json)

(defn map-key->string [k]
  (cond
    (string? k) k
    (keyword? k) (name k)
    :else
    (throw
      (ex-info
        "Ключи JSON-объекта должны быть строками или keywords."
        {:key k}))))

(defn to-json
  "Преобразует поддерживаемые EDN-значения в JSON без сторонних библиотек."
  [value]
  (cond
    (nil? value) "null"
    (true? value) "true"
    (false? value) "false"

    (string? value)
    (json-string value)

    (keyword? value)
    (json-string (name value))

    (integer? value)
    (str value)

    (instance? BigDecimal value)
    (.toPlainString ^BigDecimal value)

    (instance? Double value)
    (if (Double/isFinite (double value))
      (str value)
      (throw
        (ex-info "JSON не поддерживает NaN и Infinity." {:value value})))

    (instance? Float value)
    (if (Float/isFinite (float value))
      (str value)
      (throw
        (ex-info "JSON не поддерживает NaN и Infinity." {:value value})))

    (map? value)
    (let [entries
          (map (fn [[k v]]
                 [(map-key->string k) (to-json v)])
               value)
          keys-as-strings (map first entries)]

      (when-not
        (= (count keys-as-strings) (count (set keys-as-strings)))
        (throw
          (ex-info
            "После преобразования ключи объекта JSON не должны повторяться."
            {})))

      (str "{"
           (str/join ","
             (map (fn [[k v]]
                    (str (json-string k) ":" v))
                  entries))
           "}"))

    (sequential? value)
    (str "["
         (str/join "," (map to-json value))
         "]")

    :else
    (throw
      (ex-info
        (str "Неподдерживаемый тип EDN: " (type value))
        {:value value}))))

(defn -main [& args]
  (let [input-path (or (first args) "input.edn")
        output-path (or (second args) "output.json")]

    (try
      (when (> (count args) 2)
        (throw
          (ex-info
            "Использование: clojure -M main.clj [input.edn] [output.json]"
            {})))

      (let [input-text (slurp input-path :encoding "UTF-8")
            data (edn/read-string input-text)
            json-text (str (to-json data) "\n")]

        (spit output-path json-text :encoding "UTF-8")
        (println (str "Готово: " input-path " -> " output-path)))

      (catch FileNotFoundException e
        (binding [*out* *err*]
          (println (str "Файл не найден: " (.getMessage e))))
        (System/exit 1))

      (catch IOException e
        (binding [*out* *err*]
          (println
            (str "Ошибка чтения или записи файла: " (.getMessage e))))
        (System/exit 1))

      (catch Exception e
        (binding [*out* *err*]
          (println
            (str "Не удалось преобразовать EDN в JSON: " (.getMessage e))))
        (System/exit 1)))))

(apply -main *command-line-args*)