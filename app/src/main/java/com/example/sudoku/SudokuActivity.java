package com.example.sudoku;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sudoku.layout.SquareGridLayout;
import com.example.sudoku.sudokuBackend.SudokuGenerator;
import com.example.sudoku.sudokuBackend.SudokuGeneratorSimple;
import com.example.sudoku.sudokuBackend.SudokuTemplate;

import com.airbnb.lottie.LottieAnimationView;

public class SudokuActivity extends AppCompatActivity {
    private int GRID_SIZE;
    private int SQUARE_GRID_SIZE;
    private int SELECTED_NUMBER;
    private int COUNT_MISTAKES;
    private int COUNT_TASKS = 0;
    private int incorrectColor;
    private int defaultColor;
    private int pressedBackgroundColor;
    private int highlightedBackgroundColor;
    private int defaultBackgroundColor;
    private boolean hint;
    private int COUNT_HINTS = 3;
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

        setColors();
        generateButtonReset();
        generateBoard();
    }

    private void setColors() {
        pressedBackgroundColor = getResources().getColor(R.color.light_gray, getTheme());
        incorrectColor = getResources().getColor(R.color.red, getTheme());
        defaultColor = getResources().getColor(R.color.black, getTheme());
        highlightedBackgroundColor = R.color.blue_light;
        defaultBackgroundColor = android.R.color.background_light;
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
        createTextMistakes(COUNT_MISTAKES);
        createHintButton();
    }

    private void createTextMistakes(int mistakes) {
        TextView textMistake = findViewById(R.id.textMistake);
        textMistake.setText("Ошибки " + mistakes + "/" + 3);
    }

    private void createHintButton() {
        Button buttonHint = findViewById(R.id.buttonHint);
        buttonHint.setOnClickListener(view -> hint = !hint);
        buttonHint.setText("Подсказки " + COUNT_HINTS + "/" + "3");
        if (COUNT_HINTS == 0) {
            buttonHint.setEnabled(false);
        }
    }

    public void createTable() {
        SudokuTemplate sudokuDecision;
        if (SQUARE_GRID_SIZE == 3) {
            sudokuDecision = new SudokuGenerator(SQUARE_GRID_SIZE, DIFFICULT);
        } else {
            sudokuDecision = new SudokuGeneratorSimple(SQUARE_GRID_SIZE, DIFFICULT);
            sudokuDecision.getShuffle();
        }

        hint = false;
        COUNT_TASKS = sudokuDecision.getNumberTasks();
        COUNT_MISTAKES = 0;
        SELECTED_NUMBER = -1;
        SUDOKU_PROBLEM = sudokuDecision.deleteElements();
        NUMBERS_ON_NUMBER_BAR = new int[GRID_SIZE];

        SquareGridLayout grid_layout = findViewById(R.id.square_grid_layout);

        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {

            int row = i % GRID_SIZE, col = i / GRID_SIZE;
            int numDecisionBoard = sudokuDecision.getBoard(col, row);
            int numProblemBoard = SUDOKU_PROBLEM[col][row];

            if (numProblemBoard > 0) NUMBERS_ON_NUMBER_BAR[numProblemBoard - 1]++;

            TextView cell = new TextView(this);
            cell.setGravity(Gravity.CENTER);
            cell.setBackgroundResource(android.R.color.background_light);
            cell.setTextSize(getPixels());
            cell.setText(numProblemBoard == 0 ? "-" : String.valueOf(numProblemBoard));

            int finalI = i;

            cell.setOnClickListener(v -> {
                if (hint) {
                    showButton(numDecisionBoard, finalI);
                } else {
                    clickTable(numDecisionBoard, finalI);
                }
            });
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            int buttonSize = 13;
            params.width = buttonSize;
            params.height = buttonSize;
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
            else params.setMargins(norm, norm, norm, norm);
            grid_layout.addView(cell, params);
        }
    }

    private void clickOnTheCorrectButton(boolean userClick, int index, TextView text, int correctNumber) {

        SUDOKU_PROBLEM[index / GRID_SIZE][index % GRID_SIZE] = correctNumber;

        text.setText(String.valueOf(correctNumber));
        text.setTextColor(defaultColor);

        if (userClick) {
            text.setBackgroundColor(pressedBackgroundColor);
        }

        if (NUMBERS_ON_NUMBER_BAR[correctNumber - 1] == GRID_SIZE - 1) {
            LinearLayout linearLayout = findViewById(R.id.numberBar);
            TextView button = (TextView) linearLayout.getChildAt(correctNumber - 1);
            button.setText("");
        } else {
            NUMBERS_ON_NUMBER_BAR[correctNumber - 1]++;
        }
        COUNT_TASKS--;
        if (COUNT_TASKS == 0) {
            startAnimation(R.raw.fireworks, 1.0f, getAlertDialog("Игра окончена", "Вы выиграли. Хотите начать заново?"));
        }
    }

    private void showButton(int correctNumber, int index) {
        SquareGridLayout layout = findViewById(R.id.square_grid_layout);
        TextView text = (TextView) layout.getChildAt(index);

        if (text.getText().toString().equals("-")) {

            hint = false;
            COUNT_HINTS--;
            createHintButton();

            clickOnTheCorrectButton(false, index, text, correctNumber);
        }
    }

    private void clickTable(int correctNumber, int index) {

        SquareGridLayout layout = findViewById(R.id.square_grid_layout);
        TextView text = (TextView) layout.getChildAt(index);

        if (!text.getText().toString().equals("-") && text.getCurrentTextColor() != incorrectColor) {
            highLight(text);
            return;
        }
        if (SELECTED_NUMBER == -1) {
            return;
        }
        if (correctNumber == SELECTED_NUMBER) {
            clickOnTheCorrectButton(true, index, text, correctNumber);
        } else {
            text.setText(String.valueOf(SELECTED_NUMBER));
            text.setTextColor(incorrectColor);

            COUNT_MISTAKES++;
            createTextMistakes(COUNT_MISTAKES);
            if (COUNT_MISTAKES == 3) {
                startAnimation(R.raw.raining, 2.5f, getAlertDialog("Игра окончена", "Вы проиграли. Хотите начать заново?"));
            }
        }
    }

    private void setAllEnabled() {
        SquareGridLayout gridLayout = findViewById(R.id.square_grid_layout);
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            TextView button = (TextView) gridLayout.getChildAt(i);
            button.setEnabled(false);
        }
        LinearLayout layout = findViewById(R.id.numberBar);
        for (int i = 0; i < GRID_SIZE; i++) {
            Button button = (Button) layout.getChildAt(i);
            button.setEnabled(false);
        }
    }

    private void startAnimation(int animationCode, float speed, AlertDialog dialog) {
        setAllEnabled();
        LottieAnimationView lottieView = findViewById(R.id.lottieView);
        lottieView.setAnimation(animationCode);
        lottieView.loop(false);
        lottieView.setSpeed(speed);
        lottieView.playAnimation();
        lottieView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                dialog.show();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {
            }
        });

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

    private void highLight(TextView numberButton) {
        int oldSelectedNumber = SELECTED_NUMBER;
        if (numberButton.getText().toString().isEmpty()) {
            return;
        }
        SELECTED_NUMBER = Integer.parseInt(numberButton.getText().toString());
        if (SELECTED_NUMBER == oldSelectedNumber) {
            highLightCell(defaultBackgroundColor, numberButton.getText().toString());
            SELECTED_NUMBER = -1;
            return;
        } else {
            highLightCell(defaultBackgroundColor, Integer.toString(oldSelectedNumber));
        }
        highLightCell(highlightedBackgroundColor, numberButton.getText().toString());
    }

    private void highLightCell(int color, String secondTerm) {
        SquareGridLayout squareGridLayout = findViewById(R.id.square_grid_layout);
        for (int index = 0; index < GRID_SIZE * GRID_SIZE; index++) {
            int row = index % GRID_SIZE, col = index / GRID_SIZE;
            if (Integer.toString(SUDOKU_PROBLEM[col][row]).equals(secondTerm)) {
                TextView text = (TextView) squareGridLayout.getChildAt(index);
                if (((ColorDrawable) text.getBackground()).getColor() != pressedBackgroundColor) {
                    text.setBackgroundResource(color);
                }
            }
        }
    }


    private int getPixels() {
        switch (GRID_SIZE) {
            case (9):
                return 18;
            case (16):
                return 10;
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
            if (NUMBERS_ON_NUMBER_BAR[i] != GRID_SIZE) numberButton.setText(String.valueOf(i + 1));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(112, 112);

            numberButton.setLayoutParams(params);
            numberButton.setOnClickListener(v -> highLight(numberButton));
            numberButtonsLayout.addView(numberButton);
        }
    }

}