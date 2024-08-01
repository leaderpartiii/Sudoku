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

import org.w3c.dom.Text;

public class SudokuActivity extends AppCompatActivity {
    private int GRID_SIZE = 9;
    private int SQUARE_GRID_SIZE = 3;
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

        Button resetButton = findViewById(R.id.buttonReset);
        resetButton.setOnClickListener(view -> generateBoard());

        DIFFICULT = getIntent().getStringExtra("DIFFICULT");
        GRID_SIZE = getIntent().getIntExtra("BOARD_SIZE", 9);
        SQUARE_GRID_SIZE = (int) Math.sqrt(GRID_SIZE);

        generateBoard();
    }

    private void generateBoard() {
        createTable();
        createNumberBar();
    }

    public void createTable() {
        SudokuGenerator sudokuDecision = new SudokuGenerator(SQUARE_GRID_SIZE, DIFFICULT);

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
            cell.setTextSize(18);
            cell.setText(numProblemBoard == 0 ? "-" : String.valueOf(numProblemBoard));

            int finalI = i;
            cell.setOnClickListener(v -> showMessage(numDecisionBoard, finalI));
            defaultColor = cell.getCurrentTextColor();
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.rowSpec = GridLayout.spec(col, 1, 1f);
            params.columnSpec = GridLayout.spec(row, 1, 1f);
            if (row % SQUARE_GRID_SIZE == SQUARE_GRID_SIZE - 1 && col % SQUARE_GRID_SIZE == SQUARE_GRID_SIZE - 1) {
                params.setMargins(5, 5, 15, 15);
            } else if (row % SQUARE_GRID_SIZE == SQUARE_GRID_SIZE - 1 && col % SQUARE_GRID_SIZE != SQUARE_GRID_SIZE - 1) {
                params.setMargins(5, 5, 15, 5);
            } else if (row % SQUARE_GRID_SIZE != SQUARE_GRID_SIZE - 1 && col % SQUARE_GRID_SIZE == SQUARE_GRID_SIZE - 1) {
                params.setMargins(5, 5, 5, 15);
            } else {
                params.setMargins(5, 5, 5, 5);
            }

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
        int oldSelectedNumber;
        oldSelectedNumber = SELECTED_NUMBER;
        SELECTED_NUMBER = Integer.parseInt(numberButton.getText().toString());
        SquareGridLayout squareGridLayout = findViewById(R.id.square_grid_layout);
        if (SELECTED_NUMBER == oldSelectedNumber) {
            highLightCell(squareGridLayout, android.R.color.background_light, numberButton.getText().toString());
            SELECTED_NUMBER = -1;
            return;
        } else {
            highLightCell(squareGridLayout, android.R.color.background_light, Integer.toString(oldSelectedNumber));
        }
        highLightCell(squareGridLayout, R.color.light_gray, numberButton.getText().toString());
    }

    public void createNumberBar() {

        LinearLayout numberButtonsLayout = findViewById(R.id.numberBar);


        for (int i = 0; i < GRID_SIZE; i++) {
            Button numberButton = new Button(this);
            if (NUMBERS_ON_NUMBER_BAR[i] != GRID_SIZE) {
                numberButton.setText(String.valueOf(i + 1));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            numberButton.setLayoutParams(params);

            numberButton.setOnClickListener(v -> highLight(numberButton));

            numberButtonsLayout.addView(numberButton);
        }

    }

}