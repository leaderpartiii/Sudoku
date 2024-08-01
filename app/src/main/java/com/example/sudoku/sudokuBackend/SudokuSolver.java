package com.example.sudoku.sudokuBackend;

public class SudokuSolver {
    protected final int[][] mBoard;
    protected final int mBoardSize;
    protected final int mBoxSize;
    protected boolean[][] mRowSubset;
    protected boolean[][] mColSubset;
    protected boolean[][] mBoxSubset;

    public SudokuSolver(int[][] board) {

        mBoard = new int[board.length][board.length];
        for (int i = 0; i < board.length; i++)
            mBoard[i] = board[i].clone();
        mBoardSize = mBoard.length;
        mBoxSize = (int) Math.sqrt(mBoardSize);
        initSubsets();
    }

    protected void initSubsets() {
        mRowSubset = new boolean[mBoardSize][mBoardSize];
        mColSubset = new boolean[mBoardSize][mBoardSize];
        mBoxSubset = new boolean[mBoardSize][mBoardSize];
        for (int i = 0; i < mBoardSize; i++) {
            for (int j = 0; j < mBoardSize; j++) {
                int value = mBoard[i][j];
                if (value != 0) {
                    setSubsetValue(i, j, value - 1, true);
                }
            }
        }
    }

    protected void setSubsetValue(int i, int j, int value, boolean present) {
        mRowSubset[i][value] = present;
        mColSubset[j][value] = present;
        mBoxSubset[computeBoxNo(i, j)][value] = present;
    }

    public boolean solve() {
        return solve(0, 0);
    }

    protected boolean solve(int i, int j) {
        if (i == mBoardSize) {
            i = 0;
            if (++j == mBoardSize) {
                return true;
            }
        }
        if (mBoard[i][j] != 0) {
            return solve(i + 1, j);
        }
        for (int value = 1; value <= mBoardSize; value++) {
            if (isValid(i, j, value)) {
                mBoard[i][j] = value;
                setSubsetValue(i, j, value - 1, true);
                if (solve(i + 1, j))
                    return true;
                setSubsetValue(i, j, value - 1, false);
            }
        }

        mBoard[i][j] = 0;
        return false;
    }

    protected boolean isValid(int i, int j, int val) {
        val--;
        boolean isPresent = mRowSubset[i][val] || mColSubset[j][val] || mBoxSubset[computeBoxNo(i, j)][val];
        return !isPresent;
    }

    protected int computeBoxNo(int i, int j) {
        int boxRow = i / mBoxSize;
        int boxCol = j / mBoxSize;
        return boxRow * mBoxSize + boxCol;
    }

}
