(* Merge Sort: рекурсивное разделение списка и слияние отсортированных частей. *)

let rec split_alternating values =
  match values with
  | [] -> ([], [])
  | [x] -> ([x], [])
  | x :: y :: rest ->
      let (left, right) = split_alternating rest in
      (x :: left, y :: right)

let rec merge left right =
  match (left, right) with
  | ([], values) -> values
  | (values, []) -> values
  | (x :: xs, y :: ys) ->
      if x <= y then x :: merge xs right
      else y :: merge left ys

let rec merge_sort values =
  match values with
  | [] -> []
  | [_] -> values
  | _ ->
      let (left, right) = split_alternating values in
      merge (merge_sort left) (merge_sort right)

let read_all_stdin () =
  let buffer = Buffer.create 128 in
  (try
     while true do
       Buffer.add_channel buffer stdin 1
     done
   with End_of_file -> ());
  Buffer.contents buffer

let parse_integers input =
  let normalized =
    String.map
      (function '\t' | '\n' | '\r' -> ' ' | ch -> ch)
      input
  in
  let tokens =
    String.split_on_char ' ' normalized
    |> List.filter (fun token -> token <> "")
  in
  let rec parse = function
    | [] -> Some []
    | token :: rest ->
        (try
           let number = int_of_string token in
           match parse rest with
           | Some numbers -> Some (number :: numbers)
           | None -> None
         with Failure _ -> None)
  in
  parse tokens

let () =
  let input = read_all_stdin () in
  match parse_integers input with
  | None ->
      prerr_endline "Ошибка: введите только целые числа, разделённые пробелами.";
      exit 1
  | Some numbers ->
      let result = merge_sort numbers in
      print_endline (String.concat " " (List.map string_of_int result))
