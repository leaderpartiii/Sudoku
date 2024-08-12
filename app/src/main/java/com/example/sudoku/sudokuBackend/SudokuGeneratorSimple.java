package com.example.sudoku.sudokuBackend;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.IntBinaryOperator;

public class SudokuGeneratorSimple implements SudokuTemplate {
    protected int mBoardSize;
    protected int mSquareSize;
    protected String difficulty;
    protected final int[][] mBoard;
    protected Map<String, Integer> difficult = new HashMap<>();

    public SudokuGeneratorSimple(int[][] board, String diff) {
        if (!isValid(board))
            throw new ArithmeticException("Table is not valid ");
        mBoardSize = board.length;
        mSquareSize = (int) Math.sqrt(mBoardSize);
        mBoard = board;
        difficulty = diff;
        difficult.put("easy", 45 + new Random().nextInt(5));
        difficult.put("medium", 65 + new Random().nextInt(5));
        difficult.put("hard", 75 + new Random().nextInt(5));
    }

    public SudokuGeneratorSimple(int[][] board) {
        this(board, "easy");
    }

    public SudokuGeneratorSimple(int squareSize) {
        this(initialBoard(squareSize), "easy");
    }

    public SudokuGeneratorSimple(int squareSize, String diff) {
        this(initialBoard(squareSize), diff);
    }

    public SudokuGeneratorSimple() {
        this(3, "easy");
    }

    private static int[][] initialBoard(int squareSize) {
        int[][] temp = new int[squareSize * squareSize][squareSize * squareSize];
        for (int i = 0; i < squareSize * squareSize; i++)
            for (int j = 0; j < squareSize * squareSize; j++)
                temp[i][j] = (i * squareSize + i / squareSize + j) % (squareSize * squareSize) + 1;
        return temp;
    }

    public int[] generatePairInBox() {
        int[] pair = new int[2];
        Random rnd = new Random();
        pair[0] = rnd.nextInt(mSquareSize) * mSquareSize;
        pair[1] = pair[0] + 1 + rnd.nextInt(mSquareSize - 1);
        return pair;
    }

    public int[] generatePairBox() {
        int[] pair = new int[2];
        Random rnd = new Random();
        pair[0] = rnd.nextInt(mSquareSize);
        pair[1] = (pair[0] + 1 + rnd.nextInt(mSquareSize - 1)) % mSquareSize;
        return pair;
    }

    public void transpose() {
        for (int i = 1; i < mBoardSize; i++)
            for (int j = 0; j < i; j++)
                swapInBoard(i, j, j, i);
    }

    private void swapInBoard(int i1, int j1, int i2, int j2) {
        int tmp = mBoard[i1][j1];
        mBoard[i1][j1] = mBoard[i2][j2];
        mBoard[i2][j2] = tmp;
    }

    private void swapInTheBox(int index1, int index2, boolean isRow, boolean audit) {
        if (index2 < index1) {
            swapInTheBox(index2, index1, isRow, audit);
            return;
        }
        for (int i = 0; i < mBoardSize; i++) {
            if (isRow) swapInBoard(index1, i, index2, i);
            else swapInBoard(i, index1, i, index2);
        }
    }

    private void swapInTheBox(int index1, int index2, boolean isRow) {
        swapInTheBox(index1, index2, isRow, true);
    }

    protected void swapInTheBoxRow(int row1, int row2) {
        swapInTheBox(row1, row2, true);
    }

    protected void swapInTheBoxColumn(int col1, int col2) {
        swapInTheBox(col1, col2, false);
    }

    private void swapInTheBoxRow(int row1, int row2, boolean audit) {
        swapInTheBox(row1, row2, true, audit);
    }

    private void swapInTheBoxColumn(int col1, int col2, boolean audit) {
        swapInTheBox(col1, col2, false, audit);
    }

    protected void swapInTheBoxRow() {
        int[] pair = generatePairInBox();
        swapInTheBox(pair[0], pair[1], true);
    }

    protected void swapInTheBoxColumn() {
        int[] pair = generatePairInBox();
        swapInTheBox(pair[0], pair[1], false);
    }


    private void swapBoxes(int index1, int index2, boolean isRow) {
        if (index2 < index1) {
            swapBoxes(index2, index1, isRow);
            return;
        }
        for (int k = 0; k < mSquareSize; k++) {
            if (isRow) swapInTheBoxRow(index1 * mSquareSize + k, index2 * mSquareSize + k, false);
            else swapInTheBoxColumn(index1 * mSquareSize + k, index2 * mSquareSize + k, false);
        }
    }

    protected void swapBoxesColumn(int col1, int col2) {
        swapBoxes(col1, col2, false);
    }

    protected void swapBoxesRow(int row1, int row2) {
        swapBoxes(row1, row2, true);
    }

    protected void swapBoxesColumn() {
        int[] pair = generatePairBox();
        swapBoxes(pair[0], pair[1], false);
    }


    protected void swapBoxesRow() {
        int[] pair = generatePairBox();
        swapBoxes(pair[0], pair[1], true);
    }

    protected void swapNumbers() {
        Random rnd = new Random();
        for (int i = 0; i < mBoardSize; i++) {
            int rndNum = rnd.nextInt(mBoardSize);
            swapNumbers(i, rndNum);
        }
    }

    private void swapNumbers(int a1, int a2) {
        for (int i = 0; i < mBoardSize; i++) {
            for (int j = 0; j < mBoardSize; j++) {
                if (mBoard[j][i] == a1) {
                    mBoard[j][i] = a2;
                } else if (mBoard[j][i] == a2) {
                    mBoard[j][i] = a1;
                }
            }
        }
    }

    private boolean checkTable(int boardSize, IntBinaryOperator operator) {
        for (int i = 0; i < boardSize; i++) {
            int mn = boardSize, mx = 0;
            Set<Integer> set = new HashSet<>();
            for (int j = 0; j < boardSize; j++) {
                int temp = operator.applyAsInt(i, j);
                mn = min(temp, mn);
                mx = max(temp, mx);
                set.add(temp);
            }
            if (!(mn >= 0 && mx <= boardSize && set.size() <= boardSize)) return false;
        }
        return true;
    }

    private boolean checkBox(int[][] board) {
        int squareSize = (int) Math.sqrt(board.length);
        for (int k = 0; k < squareSize; k++) {
            for (int l = 0; l < squareSize; l++) {
                int mn = board.length, mx = 0;
                Set<Integer> set = new HashSet<>();
                for (int i = 0; i < squareSize; i++) {
                    for (int j = 0; j < squareSize; j++) {
                        int temp = board[i + k * squareSize][j + l * squareSize];
                        mn = min(temp, mn);
                        mx = max(temp, mx);
                        set.add(temp);
                    }
                }
                if (!(mn >= 0 && mx <= board.length && set.size() <= board.length)) return false;
            }
        }
        return true;
    }

    private boolean isValid(int[][] board) {
        return board.length == board[0].length &&
                checkTable(board.length, (i, j) -> board[i][j]) &&
                checkTable(board.length, (i, j) -> board[j][i]) &&
                checkBox(board);
    }


    public void getShuffle() {
        Random rnd = new Random();
        int numberCycles = 2000 + rnd.nextInt(500);
        while (numberCycles != 0) {
            int funcNumber = rnd.nextInt(6);
            switch (funcNumber) {
                case 0:
                    transpose();
                    break;
                case 1:
                    swapInTheBoxRow();
                    break;
                case 2:
                    swapInTheBoxColumn();
                    break;
                case 3:
                    swapBoxesRow();
                    break;
                case 4:
                    swapBoxesColumn();
                    break;
                case 5:
                    swapNumbers();
                    break;
            }
            numberCycles -= 1;
        }
    }

    public int[][] deleteElements() {
        int numberCycles = getNumberTasks();
        int[][] board = new int[mBoardSize][mBoardSize];
        for (int i = 0; i < mBoardSize; i++) {
            System.arraycopy(mBoard[i], 0, board[i], 0, mBoardSize);
        }
        while (numberCycles != 0) {
            Random rnd = new Random();
            int i = rnd.nextInt(mBoardSize), j = rnd.nextInt(mBoardSize), tempNumber;
            if ((tempNumber = board[i][j]) == 0) {
                continue;
            }
            board[i][j] = 0;
            SudokuSolver solver = new SudokuSolver(board);
            if (!solver.solve()) {
                board[i][j] = tempNumber;
                System.out.println("it was fail");
                continue;
            }
            numberCycles -= 1;
        }
        return board;
    }

    public String toString() {
        return getString(mBoardSize, mBoard);
    }


    public static String getString(int mBoardSize, int[][] mBoard) {
        StringBuilder res = new StringBuilder();
        int squareSize = (int) Math.sqrt(mBoardSize);
        for (int i = 0; i < mBoardSize; i++) {
            if (i % squareSize == 0) res.append(" -----------------------").append("\n");
            for (int j = 0; j < mBoardSize; j++) {
                if (j % squareSize == 0) res.append("| ");
                res.append(mBoard[i][j] != 0 ? (mBoard[i][j]) : "-");
                res.append(' ');
            }
            res.append("|").append("\n");
        }
        res.append(" -----------------------").append("\n");
        return res.toString();
    }

    @Override
    public int getNumberTasks() {
        return difficult.get(difficulty);
    }

    @Override
    public int getBoard(int col, int row) {
        return mBoard[col][row];
    }
}

