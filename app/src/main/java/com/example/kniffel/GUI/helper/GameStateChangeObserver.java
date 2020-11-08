package com.example.kniffel.GUI.helper;

import com.example.kniffel.GUI.activities.Notifiable;

import java.util.Observable;
import java.util.Observer;

public class GameStateChangeObserver implements Observer {

    private final Notifiable activity;

    public GameStateChangeObserver(Notifiable activity) {
        this.activity = activity;
    }

    @Override
    public void update(Observable o, Object arg) {
        activity.onNotify();
    }
}
