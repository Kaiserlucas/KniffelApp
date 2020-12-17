package com.example.kniffel.GUI.helper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.kniffel.GUI.activities.Notifiable;

import java.io.DataInputStream;
import java.io.IOException;

import kniffel.protocolBinding.Commands;

public class StartGameListenerThread implements Runnable {

    private static final String GAME_ABORTED_MESSAGE = "Spiel abgebrochen. Eventuell bist du kein Teilnehmer des zu ladenden Spiels.";
    private final Context context;
    private Notifiable activity;
    private DataInputStream dis;

    public StartGameListenerThread(Context context, Notifiable notifiable, DataInputStream dis) {
        this.activity = notifiable;
        this.dis = dis;
        this.context = context;

        Thread listenerThread = new Thread(this);
        listenerThread.start();
    }

    @Override
    public void run() {

        try {
            int command = dis.readInt();
            if(command == Commands.START_GAME) {
                EngineStorage.gameloaded = false;
                activity.onNotify();
            } else if(command == Commands.START_LOADED_GAME) {
                EngineStorage.gameloaded = true;
                activity.onNotify();
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, GAME_ABORTED_MESSAGE, Toast.LENGTH_LONG).show();
                    }
                });

                activity.finishSignal();
            }
        } catch (IOException e) {
            activity.finishSignal();
        }

    }
}
