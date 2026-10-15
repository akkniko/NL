#lang racket

;; Рекурсивный алгоритм Евклида для вычисления НОД.
;; НОД возвращается неотрицательным; НОД(0, 0) не определён.

(define (gcd-rec a b)
  (if (= b 0)
      (abs a)
      (gcd-rec b (remainder a b))))

(define (fail message)
  (eprintf "Ошибка: ~a\n" message)
  (exit 1))

;; Программа читает два целых числа из стандартного ввода.
(define a (read))
(define b (read))

(cond
  [(or (eof-object? a) (eof-object? b))
   (fail "нужно ввести два целых числа, например: 48 18")]
  [(not (and (exact-integer? a) (exact-integer? b)))
   (fail "оба значения должны быть целыми числами")]
  [(and (= a 0) (= b 0))
   (fail "НОД для пары (0, 0) не определён")]
  [else
   (displayln (gcd-rec a b))])
