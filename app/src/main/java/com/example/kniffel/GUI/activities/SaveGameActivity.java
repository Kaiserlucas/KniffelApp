package com.example.kniffel.GUI.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kniffel.GUI.helper.EngineStorage;
import com.example.kniffel.R;
import com.example.kniffel.persistence.SaveDataPersistence;

import java.io.IOException;

import kniffel.KniffelFacade;

public class SaveGameActivity extends AppCompatActivity {

    private static final String SAVE_FAILED_MESSAGE = "Speichern fehlgeschlagen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_game);
    }


    public void onClickDontSaveButton(View view) {
        finish();
    }

    public void onClickSaveButton(View view) {
        SaveDataPersistence persistence = new SaveDataPersistence(getFilesDir());
        KniffelFacade facade = EngineStorage.facadeStorage.get("Facade");
        String saveName = ((EditText) findViewById(R.id.saveNameTextInput)).getText().toString();

        try {
            persistence.saveGame(facade, saveName);
        } catch (IOException e) {
            Toast.makeText(this, SAVE_FAILED_MESSAGE, Toast.LENGTH_LONG).show();
        }

        finish();
    }
}