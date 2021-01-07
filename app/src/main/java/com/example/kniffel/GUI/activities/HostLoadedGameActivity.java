package com.example.kniffel.GUI.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kniffel.GUI.helper.EngineStorage;
import com.example.kniffel.R;
import com.example.kniffel.bluetooth.BluetoothConnection;
import com.example.kniffel.bluetooth.BluetoothServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kniffel.KniffelFacade;
import kniffel.KniffelFacadeFactory;
import kniffel.data.ScoreTable;
import kniffel.gamelogic.KniffelException;
import kniffel.protocolBinding.Commands;

public class HostLoadedGameActivity extends AppCompatActivity implements Notifiable {

    private static final String NO_BLUETOOTH_ADAPTER_MESSAGE = "Kein Bluetooth Adapter gefunden. Spiel kann nicht gestartet werden.";
    private static final String CONNECTION_LOST_MESSAGE = "Verbindungsfehler.";
    private static final String GAME_ABORTED_MESSAGE = "Spiel abgebrochen.";
    private int numberOfPlayers;
    private int expectedPlayers;
    private int ownPlayerID;
    private DataInputStream[] dis;
    private DataOutputStream[] dos;
    private String[] names;
    private BluetoothConnection connection;
    private TextView textView;
    private ScoreTable loadedTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_loaded_game);

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        //Initialize all required parameters for the game
        loadedTable = EngineStorage.scoreTable;
        numberOfPlayers = 1;
        expectedPlayers = loadedTable.getNumberOfPlayers();
        ownPlayerID = 1;
        dis = new DataInputStream[expectedPlayers-1];
        dos = new DataOutputStream[expectedPlayers-1];
        names = loadedTable.getPlayerNames();


        names[0] = Settings.Secure.getString(getContentResolver(), "bluetooth_name");

        startNewConnectionAttempt();

        if(expectedPlayers < 4) {
            TextView textView = findViewById(R.id.playerIDDisplay3LoadTextView);
            textView.setText("");
            textView = findViewById(R.id.playerNameDisplay3LoadTextView);
            textView.setText("");
            if(expectedPlayers < 3) {
                textView = findViewById(R.id.playerIDDisplay2LoadTextView);
                textView.setText("");
                textView = findViewById(R.id.playerNameDisplay2LoadTextView);
                textView.setText("");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connection.stopConnection();
    }

    @Override
    public void onNotify() {
        boolean isPartOfTheGame = false;

        //Reads the required data from the connection
        try {
            for(int i = 1; i < expectedPlayers; i++) {
                if(names[i] != null && names[i].equals(connection.getName()) && dis[i-1] == null) {
                    isPartOfTheGame = true;
                    dis[i-1] = connection.getDataInputStream();
                    dos[i-1] = connection.getDataOutputStream();
                    numberOfPlayers++;

                    //New connection attempt if there are still open player spots left
                    if(numberOfPlayers < expectedPlayers) {
                        startNewConnectionAttempt();
                    }

                    //Set name for the player that just connected
                    switch(i+1) {
                        case 2:
                            textView = findViewById(R.id.playerNameDisplay1LoadTextView);
                            break;
                        case 3:
                            textView = findViewById(R.id.playerNameDisplay2LoadTextView);
                            break;
                        case 4:
                            textView = findViewById(R.id.playerNameDisplay3LoadTextView);
                            break;
                    }

                    final int nameIndex = i;

                    new Thread() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText(names[nameIndex]);
                                }
                            });

                        }
                    }.start();
                    break;
                }
            }
        } catch(IOException e) {
            Toast.makeText(this, CONNECTION_LOST_MESSAGE, Toast.LENGTH_LONG).show();
            finish();
        }

        if(!isPartOfTheGame) {
            try {
                connection.getDataOutputStream().writeInt(-1);
            } catch (IOException e) {
                Toast.makeText(this, CONNECTION_LOST_MESSAGE, Toast.LENGTH_LONG).show();
            }
            startNewConnectionAttempt();
        }

        if(numberOfPlayers == expectedPlayers) {
            startGame();
        }

    }

    @Override
    public void finishSignal() {
        Toast.makeText(this, GAME_ABORTED_MESSAGE, Toast.LENGTH_LONG).show();
        finish();
    }

    private void startGame() {
        Intent intent = new Intent(this, IngameActivity.class);

        KniffelFacade facade = KniffelFacadeFactory.getLoadedGameFacade(loadedTable,dos,dis);
        EngineStorage.facadeStorage.put("Facade", facade);
        notifyOtherPlayersOfGameStart();

        startActivity(intent);
        finish();
    }


    private void startNewConnectionAttempt() {
        try {
            connection = new BluetoothServer(this);
        } catch (IOException e) {
            Toast.makeText(this, NO_BLUETOOTH_ADAPTER_MESSAGE, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void notifyOtherPlayersOfGameStart() {
        for(int i = 0; i < dos.length; i++) {
            try {
                DataOutputStream currentDos = dos[i];
                currentDos.writeInt(Commands.START_LOADED_GAME);
                currentDos.writeInt(numberOfPlayers);
                for (String name : names) {
                    currentDos.writeUTF(name);
                }
                //This is the other player's ID
                currentDos.writeInt(i + 2);
                //Write loaded game scores
                for(int j = 0; j < expectedPlayers; j++) {
                    for(int k = 0; k < KniffelFacade.SCORE_TABLE_DIM; k++) {
                        currentDos.writeInt(loadedTable.getScore(k, j+1));
                    }
                }
                currentDos.writeInt(loadedTable.getNextPlayer());
            } catch(IOException | KniffelException e) {
                Looper.prepare();
                Toast.makeText(this, CONNECTION_LOST_MESSAGE, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}