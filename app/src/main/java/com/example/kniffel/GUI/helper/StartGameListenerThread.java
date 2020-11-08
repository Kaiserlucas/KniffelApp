package com.example.kniffel.GUI.helper;

import com.example.kniffel.GUI.activities.Notifiable;

import java.io.DataInputStream;
import java.io.IOException;

import kniffel.protocolBinding.Commands;

public class StartGameListenerThread implements Runnable {

    private Notifiable activity;
    private DataInputStream dis;

    public StartGameListenerThread(Notifiable notifiable, DataInputStream dis) {
        this.activity = notifiable;
        this.dis = dis;

        Thread listenerThread = new Thread(this);
        listenerThread.start();
    }

    @Override
    public void run() {

        try {
            int command = dis.readInt();
            if(command == Commands.START_GAME) {
                activity.onNotify();
            } else {
                //TODO: Handle (Print error and return to main activity)
            }
        } catch (IOException e) {
            //TODO: (Print error and return to main activity)
        }

    }
}