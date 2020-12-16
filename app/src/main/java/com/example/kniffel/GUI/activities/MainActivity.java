package com.example.kniffel.GUI.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.kniffel.GUI.helper.EngineStorage;
import com.example.kniffel.R;
import com.example.kniffel.persistence.SaveDataPersistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import kniffel.KniffelFacade;
import kniffel.KniffelFacadeFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickLoadGameButton(View view) {
        Intent intent = new Intent(this, LoadGameActivity.class);
        startActivity(intent);
    }

    public void onClickHostGameButton(View view) {
        Intent intent = new Intent(this, HostGameActivity.class);
        startActivity(intent);
    }

    public void onClickJoinGameButton(View view) {
        Intent intent = new Intent(this, JoinGameActivity.class);
        startActivity(intent);
    }

    public void onClickDeleteGameButton(View view) {
        Intent intent = new Intent(this, DeleteGameActivity.class);
        startActivity(intent);
    }

    public void onClickDebugButton(View view){
        KniffelFacade facade = KniffelFacadeFactory.produceKniffelFacade(1,new String[0],1, new DataOutputStream[] {new DataOutputStream(new ByteArrayOutputStream())},new DataInputStream[] {new DataInputStream(new ByteArrayInputStream(new byte[0]))});
        EngineStorage.facadeStorage.put("Facade", facade);
        Intent intent = new Intent(this, IngameActivity.class);
        startActivity(intent);
    }
}