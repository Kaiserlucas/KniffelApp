package com.example.kniffel.GUI.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kniffel.GUI.helper.EngineStorage;
import com.example.kniffel.R;

import kniffel.data.ScoreTableRows;
import kniffel.gamelogic.KniffelException;
import kniffel.KniffelFacade;

public class ResultsActivity extends AppCompatActivity {

    private static final String TABLE_ROW_EXCEPTION_MESSAGE = "Wenn diese Fehlermeldung erscheint bin ich verwirrt.";
    private KniffelFacade facade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        facade = EngineStorage.facadeStorage.get("Facade");

        String[] names = facade.getPlayerNames();
        int[] scores = new int[facade.getNumberOfPlayers()];

        for(int i = 0; i < scores.length; i++) {
            try {
                scores[i] = facade.getScore(ScoreTableRows.GRAND_TOTAL, i+1);
            } catch (KniffelException e) {
                Toast.makeText(this, TABLE_ROW_EXCEPTION_MESSAGE, Toast.LENGTH_LONG).show();
            }
        }

        //TODO: Sort scores and names

        TextView textView;
        //Draw player 1 name
        textView = findViewById(R.id.playerOneNameTextView);
        textView.setText(names[0]);
        textView = findViewById(R.id.pointsText1TextView);
        textView.setText(R.string.points);

        //Draw player 2 name
        textView = findViewById(R.id.playerTwoNameTextView);
        textView.setText(names[1]);
        textView = findViewById(R.id.pointsText2TextView);
        textView.setText(R.string.points);

        //Draw player 3 name
        if(facade.getNumberOfPlayers() > 2) {
            textView = findViewById(R.id.playerThreeNameTextView);
            textView.setText(names[2]);
            textView = findViewById(R.id.pointsText3TextView);
            textView.setText(R.string.points);

            //Draw player 4 name
            if(facade.getNumberOfPlayers() > 3) {
                textView = findViewById(R.id.playerFourNameTextView);
                textView.setText(names[3]);
                textView = findViewById(R.id.pointsText4TextView);
                textView.setText(R.string.points);
            }
        }

        boolean gameInterrupted = false;

        for (int score : scores) {
            if (score == -1) {
                gameInterrupted = true;
                break;
            }
        }

        if(!gameInterrupted) {
            //Print player scores since the game actually finished
            textView = findViewById(R.id.playerOneScoreTextView);
            textView.setText(String.valueOf(scores[0]));

            textView = findViewById(R.id.playerTwoScoreTextView);
            textView.setText(String.valueOf(scores[1]));

            if(facade.getNumberOfPlayers() > 2) {
                textView = findViewById(R.id.playerThreeScoreTextView);
                textView.setText(String.valueOf(scores[2]));

                if(facade.getNumberOfPlayers() > 3) {
                    textView = findViewById(R.id.playerFourScoreTextView);
                    textView.setText(String.valueOf(scores[3]));
                }
            }
        } else {
            //print N/A for the scores of all participating players
            //Also set the results screen text to say that the game was interrupted
            textView = findViewById(R.id.resultsScreenTextView);
            textView.setText(R.string.game_interrupted);

            textView = findViewById(R.id.playerOneScoreTextView);
            textView.setText(R.string.points_na);

            textView = findViewById(R.id.playerTwoScoreTextView);
            textView.setText(R.string.points_na);

            if(facade.getNumberOfPlayers() > 2) {
                textView = findViewById(R.id.playerThreeScoreTextView);
                textView.setText(R.string.points_na);

                if(facade.getNumberOfPlayers() > 3) {
                    textView = findViewById(R.id.playerFourScoreTextView);
                    textView.setText(R.string.points_na);
                }
            }
        }
    }

    public void onClickScoreTableButton(View view) {
        Intent intent = new Intent(this, ScoreTableActivity.class);
        startActivity(intent);
    }

    public void onClickMainMenuButton(View view) {
        finish();
    }
}