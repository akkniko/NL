module Main (main) where

import Data.List (intercalate)
import System.Exit (exitFailure)
import System.IO (hPutStrLn, stderr)
import Text.Read (readMaybe)

-- QuickSort with three-way partitioning.
-- Keeping values equal to the pivot in their own group avoids repeatedly
-- recursing through a large group of duplicate values.
quickSort :: Ord a => [a] -> [a]
quickSort [] = []
quickSort (pivot : rest) =
    quickSort smaller ++ [pivot] ++ equal ++ quickSort larger
  where
    smaller = [x | x <- rest, x < pivot]
    equal   = [x | x <- rest, x == pivot]
    larger  = [x | x <- rest, x > pivot]

parseIntegers :: String -> Maybe [Integer]
parseIntegers input = traverse readMaybe (words input)

main :: IO ()
main = do
    input <- getContents
    case parseIntegers input of
        Just numbers -> putStrLn (intercalate " " (map show (quickSort numbers)))
        Nothing -> do
            hPutStrLn stderr "Ошибка: введите только целые числа, разделённые пробелами."
            exitFailure
