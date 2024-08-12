package com.example.sudoku.sudokuBackend;

import static com.example.sudoku.sudokuBackend.SudokuSolver.getString;

public interface SudokuTemplate {

    int getNumberTasks();

    int[][] deleteElements();

    int getBoard(int col, int row);

    static String toStringSudoku(int boardSize, int[][] board) {
        return getString(boardSize, board);
    }
    void getShuffle();
}
