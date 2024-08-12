package com.example.sudoku;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.sudoku.layout.SquareGridLayout;
import com.example.sudoku.sudokuBackend.SudokuGenerator;
import com.example.sudoku.sudokuBackend.SudokuGeneratorSimple;
import com.example.sudoku.sudokuBackend.SudokuTemplate;


public class SudokuActivity extends AppCompatActivity {
    private int GRID_SIZE;
    private int SQUARE_GRID_SIZE;
    private int SELECTED_NUMBER;
    private int COUNT_MISTAKES;
    private int COUNT_TASKS = 0;
    private int defaultColor;
    private int[][] SUDOKU_PROBLEM;
    private String DIFFICULT;
    private int[] NUMBERS_ON_NUMBER_BAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        DIFFICULT = getIntent().getStringExtra("DIFFICULT");
        GRID_SIZE = getIntent().getIntExtra("BOARD_SIZE", 9);
        SQUARE_GRID_SIZE = (int) Math.sqrt(GRID_SIZE);

        generateButtonReset();
        generateBoard();
    }

    private void generateButtonReset() {
        Button resetButton = findViewById(R.id.buttonReset);
        resetButton.setOnClickListener(view -> {
            resetButton();
            generateBoard();
        });
    }

    private void resetButton() {
        SquareGridLayout gridLayout = findViewById(R.id.square_grid_layout);
        LinearLayout linearLayout = findViewById(R.id.numberBar);

        gridLayout.removeAllViews();
        linearLayout.removeAllViews();
    }

    private void generateBoard() {
        createTable();
        createNumberBar();
    }

    public void createTable() {
        SudokuTemplate sudokuDecision;
        if (SQUARE_GRID_SIZE == 3) {
            sudokuDecision = new SudokuGenerator(SQUARE_GRID_SIZE, DIFFICULT);
        } else {
            sudokuDecision = new SudokuGeneratorSimple(SQUARE_GRID_SIZE, DIFFICULT);
            sudokuDecision.getShuffle();
        }

        COUNT_TASKS = sudokuDecision.getNumberTasks();
        COUNT_MISTAKES = 3;
        SELECTED_NUMBER = -1;
        SUDOKU_PROBLEM = sudokuDecision.deleteElements();
        NUMBERS_ON_NUMBER_BAR = new int[GRID_SIZE];

        SquareGridLayout grid_layout = findViewById(R.id.square_grid_layout);

        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {

            int row = i % GRID_SIZE, col = i / GRID_SIZE;
            int numDecisionBoard = sudokuDecision.getBoard(col, row);
            int numProblemBoard = SUDOKU_PROBLEM[col][row];

            if (numProblemBoard > 0)
                NUMBERS_ON_NUMBER_BAR[numProblemBoard - 1]++;

            TextView cell = new TextView(this);
            cell.setGravity(Gravity.CENTER);
            cell.setBackgroundResource(android.R.color.background_light);
            cell.setTextSize(getPixels());
            cell.setText(numProblemBoard == 0 ? "-" : String.valueOf(numProblemBoard));

            int finalI = i;
            defaultColor = cell.getCurrentTextColor();
            cell.setOnClickListener(v -> showMessage(numDecisionBoard, finalI));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.rowSpec = GridLayout.spec(col, 1, 1f);
            params.columnSpec = GridLayout.spec(row, 1, 1f);

            int norm = 5;
            int space = 15;
            if (row % SQUARE_GRID_SIZE == SQUARE_GRID_SIZE - 1 && col % SQUARE_GRID_SIZE == SQUARE_GRID_SIZE - 1)
                params.setMargins(norm, norm, space, space);
            else if (row % SQUARE_GRID_SIZE == SQUARE_GRID_SIZE - 1 && col % SQUARE_GRID_SIZE != SQUARE_GRID_SIZE - 1)
                params.setMargins(norm, norm, space, norm);
            else if (row % SQUARE_GRID_SIZE != SQUARE_GRID_SIZE - 1 && col % SQUARE_GRID_SIZE == SQUARE_GRID_SIZE - 1)
                params.setMargins(norm, norm, norm, space);
            else
                params.setMargins(norm, norm, norm, norm);
            grid_layout.addView(cell, params);
        }
    }

    public void showMessage(int number, int index) {
        SquareGridLayout layout = findViewById(R.id.square_grid_layout);
        TextView text = (TextView) layout.getChildAt(index);

        if (!text.getText().toString().equals("-") && text.getCurrentTextColor() != ContextCompat.getColor(this, R.color.red)) {
            highLight(text);
            return;
        }

        if (number == SELECTED_NUMBER) {
            SUDOKU_PROBLEM[index / GRID_SIZE][index % GRID_SIZE] = SELECTED_NUMBER;
            text.setText(String.valueOf(SELECTED_NUMBER));
            text.setTextColor(defaultColor);

            if (NUMBERS_ON_NUMBER_BAR[number - 1] == GRID_SIZE - 1) {
                LinearLayout linearLayout = findViewById(R.id.numberBar);
                TextView button = (TextView) linearLayout.getChildAt(number - 1);
                button.setText("");
            } else {
                NUMBERS_ON_NUMBER_BAR[number - 1]++;
            }
            COUNT_TASKS--;
            if (COUNT_TASKS == 0) {
                AlertDialog dialog = getAlertDialog("Игра окончена", "Вы выиграли. Хотите начать заново?");
                dialog.show();
            }
        } else {
            text.setText(String.valueOf(SELECTED_NUMBER));
            text.setTextColor(getResources().getColor(R.color.red));

            COUNT_MISTAKES--;
            if (COUNT_MISTAKES == 0) {
                AlertDialog dialog = getAlertDialog("Игра окончена", "Вы проиграли. Хотите начать заново?");
                dialog.show();
            }
        }
    }

    private AlertDialog getAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton("Начать заново", (dialog, which) -> {
            Intent intent = new Intent(SudokuActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton("Перегенерировать поле", (dialog, which) -> {
            resetButton();
            generateBoard();
            finish();
        });

        return builder.create();
    }

    private void highLightCell(SquareGridLayout squareGridLayout, int color, String secondTerm) {
        for (int index = 0; index < GRID_SIZE * GRID_SIZE; index++) {
            int row = index % GRID_SIZE, col = index / GRID_SIZE;
            if (Integer.toString(SUDOKU_PROBLEM[col][row]).equals(secondTerm)) {
                TextView text = (TextView) squareGridLayout.getChildAt(index);
                text.setBackgroundResource(color);
            }
        }
    }

    private void highLight(TextView numberButton) {
        int oldSelectedNumber = SELECTED_NUMBER;
        SquareGridLayout squareGridLayout = findViewById(R.id.square_grid_layout);
        SELECTED_NUMBER = Integer.parseInt(numberButton.getText().toString());
        if (SELECTED_NUMBER == oldSelectedNumber) {
            highLightCell(squareGridLayout, android.R.color.background_light, numberButton.getText().toString());
            SELECTED_NUMBER = -1;
            return;
        } else {
            highLightCell(squareGridLayout, android.R.color.background_light, Integer.toString(oldSelectedNumber));
        }
        highLightCell(squareGridLayout, R.color.light_gray, numberButton.getText().toString());
    }

    private int getPixels() {
        switch (GRID_SIZE) {
            case (9):
                return 18;
            case (16):
                return 8;
            case (25):
                return 4;
            default:
                return 0;
        }
    }

    public void createNumberBar() {
        LinearLayout numberButtonsLayout = findViewById(R.id.numberBar);
        for (int i = 0; i < GRID_SIZE; i++) {
            Button numberButton = new Button(this);
            if (NUMBERS_ON_NUMBER_BAR[i] != GRID_SIZE)
                numberButton.setText(String.valueOf(i + 1));
            numberButton.setTextSize(getPixels());
            numberButton.setPadding(10, 20, 10, 20);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

            numberButton.setLayoutParams(params);
            numberButton.setOnClickListener(v -> highLight(numberButton));
            numberButtonsLayout.addView(numberButton);
        }
    }

}