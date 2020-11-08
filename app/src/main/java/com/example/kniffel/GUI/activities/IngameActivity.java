package com.example.kniffel.GUI.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kniffel.GUI.helper.EngineStorage;
import com.example.kniffel.GUI.helper.GameStateChangeObserver;
import com.example.kniffel.R;

import java.io.IOException;

import kniffel.data.ScoreTableRows;
import kniffel.gamelogic.GameState;
import kniffel.gamelogic.IllegalStateException;
import kniffel.gamelogic.KniffelException;
import kniffel.KniffelFacade;

public class IngameActivity extends AppCompatActivity implements Notifiable {

    private KniffelFacade facade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingame);

        facade = EngineStorage.facadeStorage.get("Facade");
        assert facade != null;

        facade.addObserver(new GameStateChangeObserver(this));

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
                facade.saveGame();
            } catch (IOException e) {
                //TODO: Handle
            }
        }
    }

    @Override
    public void onBackPressed() {
        //Disable back button for this activity to prevent accidentally tapping it.
    }

    public void onClickRollDiceButton(View view) {
        if(facade.getActivePlayer() == facade.getOwnPlayerID() && facade.getRollsRemaining() > 0) {
            try {
                facade.rollDice();
            } catch (IllegalStateException e) {
                //TODO: Handle
            } catch (IOException e) {
                //TODO: Handle
            }
            updateGUI();
        }
    }

    public void onClickEndTurnDebugButton(View view) {
        if(facade.getActivePlayer() == facade.getOwnPlayerID() && facade.getRollsRemaining() < 3) {
            try {
                //TODO: Get table row somehow
                facade.endTurn(ScoreTableRows.CHANCE);
            } catch (IllegalStateException e) {
                //TODO: Handle
            } catch (KniffelException e) {
                //TODO: Handle
            } catch (IOException e) {
                //TODO: Handle
            }
        }
        updateGUI();
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
                //TODO: Handle
            } catch (IOException e) {
                //TODO: Handle
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

        //TODO: Not sure if this gets too annoying after a while
        //If it's the start of a new turn show the score table to every player
        //if(facade.getDiceValues()[0] == -1) {
            //Intent intent = new Intent(this, ScoreTableActivity.class);
            //startActivity(intent);
        //}

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
                throw new RuntimeException("Unknown player ID");
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
                    throw new RuntimeException("Unknown Dice Value");
            }
        }
    }

    @Override
    public void onNotify() {
        //TODO: Figure out why this works
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
}