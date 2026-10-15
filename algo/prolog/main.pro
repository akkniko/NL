% Проверка палиндрома без использования готового предиката reverse/2.
% Пустой список и список из одного элемента являются палиндромами.

palindrome([]).
palindrome([_]).
palindrome(List) :-
    List = [First | _],
    remove_last(List, Last, Inner),
    First = Last,
    palindrome(Inner).

% remove_last(List, Last, Prefix) succeeds when Last is the last item
% and Prefix contains all preceding items.
remove_last([Last], Last, []).
remove_last([Head, Next | Rest], Last, [Head | Prefix]) :-
    remove_last([Next | Rest], Last, Prefix).

integer_list([]).
integer_list([Number | Rest]) :-
    integer(Number),
    integer_list(Rest).

% Run as a command-line program. Enter a Prolog list terminated by a period,
% for example: [1,2,3,2,1].
main :-
    catch(read_term(user_input, List, []), Error,
          (print_message(error, Error), halt(1))),
    (   List == end_of_file
    ->  true
    ;   integer_list(List)
    ->  (   palindrome(List)
        ->  writeln(true)
        ;   writeln(false)
        )
    ;   writeln('ERROR: expected a list of integers')
    ).

:- initialization(main, main).
