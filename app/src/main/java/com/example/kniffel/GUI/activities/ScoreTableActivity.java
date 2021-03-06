package com.example.kniffel.GUI.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kniffel.GUI.adapters.ScoreTableRecyclerViewAdapter;
import com.example.kniffel.GUI.helper.EngineStorage;
import com.example.kniffel.R;

import kniffel.KniffelFacade;

public class ScoreTableActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ScoreTableRecyclerViewAdapter adapter;
    private KniffelFacade facade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_table);
        this.facade = EngineStorage.facadeStorage.get("Facade");

        //Initialize RecyclerView
        recyclerView = findViewById(R.id.score_table_recycler_view);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        adapter = new ScoreTableRecyclerViewAdapter(this, facade);
        recyclerView.setAdapter(adapter);

        TextView textView = findViewById(R.id.player1TextView);
        textView.setText(facade.getPlayerNames()[0]);
        textView = findViewById(R.id.player2TextView);
        textView.setText(facade.getPlayerNames()[1]);

        if(facade.getNumberOfPlayers() >= 3) {
            textView = findViewById(R.id.player3TextView);
            textView.setText(facade.getPlayerNames()[2]);
            if(facade.getNumberOfPlayers() == 4) {
                textView = findViewById(R.id.player4TextView);
                textView.setText(facade.getPlayerNames()[3]);
            }
        }
    }

    public void onClickBackButton(View view) {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
