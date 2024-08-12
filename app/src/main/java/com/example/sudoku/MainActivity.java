package com.example.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private int boardSize = 9;
    private String difficult = "easy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button starting_button = findViewById(R.id.starting_button);

        Button button3x3 = findViewById(R.id.button3x3);
        Button button4x4 = findViewById(R.id.button4x4);
        Button button5x5 = findViewById(R.id.button5x5);

        Button buttonEasy = findViewById(R.id.buttonEasy);
        Button buttonMedium = findViewById(R.id.buttonMedium);
        Button buttonHard = findViewById(R.id.buttonHard);


        button3x3.setOnClickListener(view -> selectBoardSize(9));
        button4x4.setOnClickListener(view -> selectBoardSize(16));
        button5x5.setOnClickListener(view -> selectBoardSize(25));

        buttonEasy.setOnClickListener(view -> selectDifficult("easy"));
        buttonMedium.setOnClickListener(view -> selectDifficult("medium"));
        buttonHard.setOnClickListener(view -> selectDifficult("hard"));

        starting_button.setOnClickListener(view -> nextActivity(view));
    }

    public void selectBoardSize(int num) {
        boardSize = num;
    }

    public void selectDifficult(String diff) {
        difficult = diff;
    }

    public void nextActivity(View view) {
        Intent intent = new Intent(this, SudokuActivity.class);
        intent.putExtra("BOARD_SIZE", boardSize);
        intent.putExtra("DIFFICULT", difficult);
        startActivity(intent);
    }
}