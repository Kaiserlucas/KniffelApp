package com.example.kniffel.GUI.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kniffel.GUI.helper.EngineStorage;
import com.example.kniffel.GUI.helper.GameStateChangeObserver;
import com.example.kniffel.R;

import java.io.IOException;

import kniffel.gamelogic.GameState;
import kniffel.gamelogic.IllegalStateException;
import kniffel.KniffelFacade;

public class IngameActivity extends AppCompatActivity implements Notifiable {


    private static final String GAME_ABORTED_MESSAGE = "Spiel abgebrochen.";
    private KniffelFacade facade;
    private static final String BACKBUTTON_MESSAGE = "Drücke diese Schaltfläche 3 Mal um das Spiel zu beenden.";
    private static final String END_GAME_ERROR_MESSAGE = "Verbindungsfehler. Nicht alle Spieler konnten benachrichtigt werden, dass das Spiel vorbei ist.";
    private static final String OPPONENT_TURN_MESSAGE = "Ein Gegenspieler ist am Zug.";
    private static final String CONNECTION_LOST_MESSAGE = "Verbindungsfehler. Spiel wird beendet.";
    private int backButtonTimesPressed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingame);

        facade = EngineStorage.facadeStorage.get("Facade");
        assert facade != null;

        facade.addObserver(new GameStateChangeObserver(this));

        //Hide save and quit button for non-host players
        if(facade.getOwnPlayerID() != 1) {
            Button button = findViewById(R.id.saveAndQuitIngameButton);
            button.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Notifies other players that the game is over
        if(facade.getState() != GameState.GameEnded) {
            try {
                facade.endGame();
            } catch (IOException e) {
                Toast.makeText(this, END_GAME_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
            }
        }
    }

    //Needs to press back button three times to prevent accidentally ending the game
    @Override
    public void onBackPressed() {
        if(backButtonTimesPressed == 2) {
            finish();
        } else {
            backButtonTimesPressed++;
            Toast.makeText(this, BACKBUTTON_MESSAGE, Toast.LENGTH_LONG).show();
        }
    }

    public void onClickSaveAndQuitButton(View view) {
        Intent intent = new Intent(this, SaveGameActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickRollDiceButton(View view) {
        if(facade.getActivePlayer() == facade.getOwnPlayerID() && facade.getRollsRemaining() > 0) {
            try {
                facade.rollDice();
            } catch (IllegalStateException e) {
                Toast.makeText(this, OPPONENT_TURN_MESSAGE, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, CONNECTION_LOST_MESSAGE, Toast.LENGTH_LONG).show();
                finish();
            }
            updateGUI();
        }
    }

    public void onClickDice(View view) {
        if(facade.getActivePlayer() == facade.getOwnPlayerID() && facade.getRollsRemaining() < 3) {
            try {
                switch (view.getId()) {
                    case R.id.setAsideDice1:
                    case R.id.inPlayDice1:
                        facade.changeDiceState(0);
                        break;
                    case R.id.setAsideDice2:
                    case R.id.inPlayDice2:
                        facade.changeDiceState(1);
                        break;
                    case R.id.setAsideDice3:
                    case R.id.inPlayDice3:
                        facade.changeDiceState(2);
                        break;
                    case R.id.setAsideDice4:
                    case R.id.inPlayDice4:
                        facade.changeDiceState(3);
                        break;
                    case R.id.setAsideDice5:
                    case R.id.inPlayDice5:
                        facade.changeDiceState(4);
                        break;
                    default:
                        throw new RuntimeException("Unknown Button ID");

                }
            } catch (IllegalStateException e) {
                Toast.makeText(this, OPPONENT_TURN_MESSAGE, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, CONNECTION_LOST_MESSAGE, Toast.LENGTH_LONG).show();
                finish();
            }

            updateGUI();
        }
    }

    public void onClickScoreTableButton(View view) {
        Intent intent = new Intent(this, ScoreTableActivity.class);
        startActivity(intent);
    }

    private void updateGUI() {

        //If the game is over send players to the results screen
        if(facade.getState() == GameState.GameEnded){
            Intent intent = new Intent(this, ResultsActivity.class);
            startActivity(intent);
            finish();
        }

        //TODO: Write own player name
        setRollsRemainingText();
        drawDiceImages();
        displayCurrentPlayer();

    }

    private void displayCurrentPlayer() {
        TextView currentPlayerDisplay = findViewById(R.id.currentPlayerDisplayTextView);

        switch(facade.getActivePlayer()) {
            case -1:
                currentPlayerDisplay.setText(R.string.game_ended);
                break;
            case 1:
                currentPlayerDisplay.setText(R.string.player_1);
                break;
            case 2:
                currentPlayerDisplay.setText(R.string.player_2);
                break;
            case 3:
                currentPlayerDisplay.setText(R.string.player_3);
                break;
            case 4:
                currentPlayerDisplay.setText(R.string.player_4);
                break;
            default:
                throw new RuntimeException("Unknown player ID "+facade.getActivePlayer());
        }
    }

    private void setRollsRemainingText() {
        TextView rollDiceButton = findViewById(R.id.rollDiceButton);
        switch(facade.getRollsRemaining()) {
            case 3:
                rollDiceButton.setText(R.string.button_roll_dice_x3);
                break;
            case 2:
                rollDiceButton.setText(R.string.button_roll_dice_x2);
                break;
            case 1:
                rollDiceButton.setText(R.string.button_roll_dice_x1);
                break;
            case 0:
                rollDiceButton.setText(R.string.button_roll_dice_x0);
                break;
            default:
                throw new RuntimeException("Illegal rolls remaining value");
        }
    }

    private void drawDiceImages() {

        ImageButton imageButtonToSet = null;
        ImageButton imageButtonToUnset = null;
        int[] diceValues = facade.getDiceValues();
        boolean[] diceIsSetAside = facade.areDicesSetAside();

        for(int i = 0; i < 5; i++) {
            //Determines which of the two locations for a dice should be filled and which should be blank
            if(diceIsSetAside[i]) {
                switch(i) {
                    case 0:
                        imageButtonToSet = findViewById(R.id.setAsideDice1);
                        imageButtonToUnset = findViewById(R.id.inPlayDice1);
                        break;
                    case 1:
                        imageButtonToSet = findViewById(R.id.setAsideDice2);
                        imageButtonToUnset = findViewById(R.id.inPlayDice2);
                        break;
                    case 2:
                        imageButtonToSet = findViewById(R.id.setAsideDice3);
                        imageButtonToUnset = findViewById(R.id.inPlayDice3);
                        break;
                    case 3:
                        imageButtonToSet = findViewById(R.id.setAsideDice4);
                        imageButtonToUnset = findViewById(R.id.inPlayDice4);
                        break;
                    case 4:
                        imageButtonToSet = findViewById(R.id.setAsideDice5);
                        imageButtonToUnset = findViewById(R.id.inPlayDice5);
                        break;
                }
            } else {
                switch(i) {
                    case 0:
                        imageButtonToSet = findViewById(R.id.inPlayDice1);
                        imageButtonToUnset = findViewById(R.id.setAsideDice1);
                        break;
                    case 1:
                        imageButtonToSet = findViewById(R.id.inPlayDice2);
                        imageButtonToUnset = findViewById(R.id.setAsideDice2);
                        break;
                    case 2:
                        imageButtonToSet = findViewById(R.id.inPlayDice3);
                        imageButtonToUnset = findViewById(R.id.setAsideDice3);
                        break;
                    case 3:
                        imageButtonToSet = findViewById(R.id.inPlayDice4);
                        imageButtonToUnset = findViewById(R.id.setAsideDice4);
                        break;
                    case 4:
                        imageButtonToSet = findViewById(R.id.inPlayDice5);
                        imageButtonToUnset = findViewById(R.id.setAsideDice5);
                        break;
                }
            }

            //Draws the actual dice values / turns the empty dice socket blank
            switch(diceValues[i]) {
                case -1:
                    imageButtonToSet.setImageResource(android.R.color.transparent);
                    imageButtonToUnset.setImageResource(android.R.color.transparent);
                    break;
                case 1:
                    imageButtonToSet.setImageResource(R.mipmap.dice1art);
                    imageButtonToUnset.setImageResource(android.R.color.transparent);
                    break;
                case 2:
                    imageButtonToSet.setImageResource(R.mipmap.dice2art);
                    imageButtonToUnset.setImageResource(android.R.color.transparent);
                    break;
                case 3:
                    imageButtonToSet.setImageResource(R.mipmap.dice3art);
                    imageButtonToUnset.setImageResource(android.R.color.transparent);
                    break;
                case 4:
                    imageButtonToSet.setImageResource(R.mipmap.dice4art);
                    imageButtonToUnset.setImageResource(android.R.color.transparent);
                    break;
                case 5:
                    imageButtonToSet.setImageResource(R.mipmap.dice5art);
                    imageButtonToUnset.setImageResource(android.R.color.transparent);
                    break;
                case 6:
                    imageButtonToSet.setImageResource(R.mipmap.dice6art);
                    imageButtonToUnset.setImageResource(android.R.color.transparent);
                    break;
                default:
                    throw new RuntimeException("Unknown Dice Value "+diceValues[i]);
            }
        }
    }

    @Override
    public void onNotify() {
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateGUI();
                    }
                });

            }
        }.start();
    }

    @Override
    public void finishSignal() {
        Toast.makeText(this, GAME_ABORTED_MESSAGE, Toast.LENGTH_LONG).show();
        finish();
    }
}