package com.example.sudoku.sudokuBackend;

import java.util.*;
import java.util.function.IntBinaryOperator;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class SudokuGenerator implements SudokuTemplate {
    protected static int mBoardSize;
    protected static int mSquareSize;
    protected String difficulty;
    protected final int[][] mBoard;
    protected int[][] mDeletedBoard;
    protected Map<String, Integer> difficult = new HashMap<>();

    public SudokuGenerator(int[][] board, String diff) {
        if (!isValid(board))
            throw new ArithmeticException("Table is not valid ");
        mBoardSize = board.length;
        mSquareSize = (int) Math.sqrt(mBoardSize);
        mBoard = board;
        difficulty = diff;
        difficult.put("easy", 20 + new Random().nextInt(5));
        difficult.put("medium", 25 + new Random().nextInt(5));
        difficult.put("hard", 30 + new Random().nextInt(5));
    }

    public SudokuGenerator(int[][] board) {
        this(board, "easy");
    }

    public SudokuGenerator(int squareSize) {
        this(initialBoard(squareSize), "easy");
    }

    public SudokuGenerator(int squareSize, String diff) {
        this(initialBoard(squareSize), diff);
    }

    public SudokuGenerator() {
        this(3, "easy");
    }

    public SudokuGenerator(String mod) {
        this(3, mod);
    }

    private static int[][] initialBoard(int squareSize) {
        int[][] board = new int[squareSize * squareSize][squareSize * squareSize];
        firstLevel(board, squareSize);
        boolean temp = secondLevel(board, squareSize);
        while (!temp) {
            board = new int[squareSize * squareSize][squareSize * squareSize];
            firstLevel(board, squareSize);
            temp = secondLevel(board, squareSize);
        }
        return board;
    }

    @Override
    public int getBoard(int i, int j) {
        return mBoard[i][j];
    }

    @Override
    public void getShuffle() {
    }

    private static ArrayList<Integer> getRandomList(int boardSize) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        return list;
    }

    private static int getUniqueNumber(int[][] board, int row, int col, int squareSize) {
        Set<Integer> set = new HashSet<>();
        for (int k = 0; k < squareSize * squareSize; k++) {
            if (board[k][col] != 0) {
                set.add(board[k][col]);
            }
            if (board[row][k] != 0) {
                set.add(board[row][k]);
            }
        }
        int rowBox = row / squareSize;
        int colBox = col / squareSize;
        for (int i = 0; i < squareSize; i++) {
            for (int j = 0; j < squareSize; j++) {
                if (board[rowBox * squareSize + i][colBox * squareSize + j] != 0)
                    set.add(board[rowBox * squareSize + i][colBox * squareSize + j]);
            }
        }
        int res;
        Random rnd = new Random();
        if (set.size() == squareSize * squareSize) {
            return -1;
        }
        do {
            res = rnd.nextInt(squareSize * squareSize) + 1;
        } while (set.contains(res));
        return res;
    }

    private static void firstLevel(int[][] board, int squareSize) {
        int diag = 0;
        while (diag != squareSize * squareSize) {
            ListIterator<Integer> nums = getRandomList(squareSize * squareSize).listIterator();
            for (int i = diag; i < diag + squareSize; i++)
                for (int j = diag; j < diag + squareSize; j++)
                    board[i][j] = nums.next() + 1;
            diag += squareSize;
        }
    }

    private static boolean secondLevel(int[][] board, int squareSize) {
        for (int i = 0; i < squareSize * squareSize; i++) {
            for (int j = 0; j < squareSize * squareSize; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = getUniqueNumber(board, i, j, squareSize);
                    if (board[i][j] == -1) {
                        return false;
                    }
                }
            }
        }
        return true;
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

    public int getNumberTasks() {
        return difficult.get(difficulty);
    }

    public int[][] deleteElements() {
        int numberCycles = getNumberTasks();
        mDeletedBoard = new int[mBoardSize][mBoardSize];
        for (int i = 0; i < mBoardSize; i++)
            mDeletedBoard[i] = mBoard[i].clone();

        while (numberCycles != 0) {
            Random rnd = new Random();
            int i = rnd.nextInt(mBoardSize), j = rnd.nextInt(mBoardSize), tempNumber;
            if ((tempNumber = mDeletedBoard[i][j]) == 0) {
                continue;
            }
            mDeletedBoard[i][j] = 0;
            SudokuSolver solver = new SudokuSolver(mDeletedBoard);
            if (!solver.solve()) {
                mDeletedBoard[i][j] = tempNumber;
                continue;
            }
            numberCycles -= 1;
        }
        return mDeletedBoard;
    }

}
