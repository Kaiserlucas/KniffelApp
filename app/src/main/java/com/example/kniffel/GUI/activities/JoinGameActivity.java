package com.example.kniffel.GUI.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kniffel.GUI.helper.EngineStorage;
import com.example.kniffel.GUI.helper.StartGameListenerThread;
import com.example.kniffel.R;
import com.example.kniffel.bluetooth.BluetoothClient;
import com.example.kniffel.bluetooth.BluetoothConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kniffel.KniffelFacade;
import kniffel.KniffelFacadeFactory;
import kniffel.data.ScoreTable;
import kniffel.data.ScoreTableImpl;

public class JoinGameActivity extends AppCompatActivity implements Notifiable {

    private static final String GAME_ABORTED_MESSAGE = "Spiel abgebrochen.";
    private BluetoothConnection connection;
    private DataInputStream[] dis;
    private DataOutputStream[] dos;
    private boolean connected;
    private TextView status;
    private TextView playerName;
    private String playerNameMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        connected = false;
        dis = new DataInputStream[1];
        dos = new DataOutputStream[1];

        try {
            this.connection = new BluetoothClient(this);
        } catch (IOException e) {
            finish();
        } catch (InterruptedException e) {
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connection.stopConnection();
    }

    @Override
    public void onNotify() {
        //On the first notify, prepare everything for network communication and listen for the game start
        //On the second notify, prepare the engine and actually start the game
        if(!connected) {
            getConnectionInfo();
        } else {
            startGame();
        }

    }

    private void getConnectionInfo() {
        String name = "I am Error";
        try {
            dis[0] = connection.getDataInputStream();
            dos[0] = connection.getDataOutputStream();
            name = connection.getName();

            StartGameListenerThread listenerThread = new StartGameListenerThread(this, this, dis[0]);
        } catch (IOException e) {
            finish();
        }
        connected = true;

        status = findViewById(R.id.joinGameStatusTextView);

        playerName = findViewById(R.id.foundPlayerNameTextView);
        playerNameMessage = name;

        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        status.setText(getString(R.string.found_player));
                        playerName.setText(playerNameMessage);
                    }
                });

            }
        }.start();
    }

    private void startGame() {
        int numberOfPlayers = 0;
        String[] names = null;
        int ownPlayerID = 0;
        int nextPlayer = 0;

        //Do regardless of new game or load
        try{
            numberOfPlayers = dis[0].readInt();
            names = new String[numberOfPlayers];
            for(int i = 0; i < numberOfPlayers; i++) {
                names[i] = dis[0].readUTF();
            }
            ownPlayerID = dis[0].readInt();
        } catch(IOException e) {
            finish();
        }

        //If normal game create Facade regularly
        //If loaded game read scoreTable and create a Facade with that
        if(!EngineStorage.gameloaded) {
            KniffelFacade facade = KniffelFacadeFactory.produceKniffelFacade(numberOfPlayers,names,ownPlayerID,dos,dis);
            EngineStorage.facadeStorage.put("Facade", facade);
        } else {
            int[][] scores = new int[KniffelFacade.SCORE_TABLE_DIM][numberOfPlayers];
            for(int i = 0; i < numberOfPlayers; i++) {
                for(int j = 0; j < KniffelFacade.SCORE_TABLE_DIM; j++) {
                    try {
                        scores[j][i] = dis[0].readInt();
                    } catch (IOException e) {
                        e.printStackTrace();
                        //TODO: Handle
                    }
                }
            }

            try {
                nextPlayer = dis[0].readInt();
            } catch (IOException e) {
                //TODO: Handle
            }

            ScoreTable scoreTable = new ScoreTableImpl(numberOfPlayers, nextPlayer, names, ownPlayerID, scores);
            KniffelFacade facade = KniffelFacadeFactory.getLoadedGameFacade(scoreTable, dos, dis);
            EngineStorage.facadeStorage.put("Facade", facade);
        }

        Intent intent = new Intent(this, IngameActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    public void finishSignal() {
        finish();
    }

}