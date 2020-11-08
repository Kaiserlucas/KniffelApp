package com.example.kniffel.GUI.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.example.kniffel.GUI.helper.EngineStorage;
import com.example.kniffel.R;
import com.example.kniffel.bluetooth.BluetoothConnection;
import com.example.kniffel.bluetooth.BluetoothServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kniffel.KniffelFacade;
import kniffel.KniffelFacadeFactory;
import kniffel.protocolBinding.Commands;


public class HostGameActivity extends AppCompatActivity implements Notifiable {

    private int numberOfPlayers;
    private int ownPlayerID;
    private DataInputStream[] dis;
    private DataOutputStream[] dos;
    private String[] names;
    private BluetoothConnection connection;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        //Initialize all required parameters for the game
        numberOfPlayers = 1;
        ownPlayerID = 1;
        dis = new DataInputStream[4];
        dos = new DataOutputStream[4];
        names = new String[4];

        names[0] = Settings.Secure.getString(getContentResolver(), "bluetooth_name");

        startNewConnectionAttempt();
    }

    @Override
    public void onNotify() {
        //Reads the required data from the connection
        try {
            dis[numberOfPlayers - 1] = connection.getDataInputStream();
            dos[numberOfPlayers - 1] = connection.getDataOutputStream();
            names[numberOfPlayers] = connection.getName();
        } catch(IOException e) {
            //TODO: Should probably have an error message too
            finish();
        }
         numberOfPlayers++;

        //New connection attempt if there are still open player spots left
        if(numberOfPlayers < 4) {
            startNewConnectionAttempt();
        }

        //Set name for the player that just connected
        switch(numberOfPlayers) {
            case 2:
                textView = findViewById(R.id.playerNameDisplay1TextView);
                break;
            case 3:
                textView = findViewById(R.id.playerNameDisplay2TextView);
                break;
            case 4:
                textView = findViewById(R.id.playerIDDisplay3TextView);
                break;
        }

        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(names[numberOfPlayers-1]);
                    }
                });

            }
        }.start();

    }

    public void onClickStartGameButton(View view) {

        if(numberOfPlayers > 1) {
            Intent intent = new Intent(this, IngameActivity.class);

            String[] namesFinal = new String[numberOfPlayers];
            DataInputStream[] disFinal = new DataInputStream[numberOfPlayers-1];
            DataOutputStream[] dosFinal = new DataOutputStream[numberOfPlayers-1];

            for(int i = 0; i < numberOfPlayers; i++) namesFinal[i] = names[i];
            for(int i = 0; i < numberOfPlayers-1; i++) disFinal[i] = dis[i];
            for(int i = 0; i < numberOfPlayers-1; i++) dosFinal[i] = dos[i];

            names = namesFinal;
            dis = disFinal;
            dos = dosFinal;

            KniffelFacade facade = KniffelFacadeFactory.produceKniffelFacade(numberOfPlayers, names, ownPlayerID, dos, dis);
            EngineStorage.facadeStorage.put("Facade", facade);
            notifyOtherPlayersOfGameStart();

            startActivity(intent);

            finish();
        }
    }


    private void startNewConnectionAttempt() {
        try {
            connection = new BluetoothServer(this);
        } catch (IOException e) {
            //TODO: Handle correctly (This is thrown if the device has no bluetooth adapter)
            finish();
        } catch (InterruptedException e) {
            //TODO: Handle correctly (This is thrown if something goes horribly wrong with the accept)
            finish();
        }
    }

    private void notifyOtherPlayersOfGameStart() {
        for(int i = 0; i < dos.length; i++) {
            try {
                DataOutputStream currentDos = dos[i];
                currentDos.writeInt(Commands.START_GAME);
                currentDos.writeInt(numberOfPlayers);
                for (String name : names) {
                    currentDos.writeUTF(name);
                }
                //This is the other player's ID
                currentDos.writeInt(i + 2);
            } catch(IOException e) {
                //TODO: Should probably have an error message
                finish();
            }
        }

    }
}