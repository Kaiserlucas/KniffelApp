package com.example.kniffel.GUI.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.kniffel.GUI.adapters.SaveGamesLoadRecyclerViewAdapter;
import com.example.kniffel.R;

import java.io.IOException;

public class LoadGameActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SaveGamesLoadRecyclerViewAdapter adapter;
    private static final String CANT_ACCESS_FILE_MESSAGE = "Speicherverzeichnis konnte nicht geöffnet werden.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        //Initialize RecyclerView
        recyclerView = findViewById(R.id.score_table_recycler_view);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        try {
            adapter = new SaveGamesLoadRecyclerViewAdapter(this, getFilesDir());
            recyclerView.setAdapter(adapter);
        } catch (IOException e) {
            Toast.makeText(this, CANT_ACCESS_FILE_MESSAGE, Toast.LENGTH_LONG).show();
        }

    }

    public void onClickBackButton(View view) {
        finish();
    }
}