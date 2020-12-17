package com.example.kniffel.GUI.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kniffel.GUI.activities.HostLoadedGameActivity;
import com.example.kniffel.GUI.helper.EngineStorage;
import com.example.kniffel.R;
import com.example.kniffel.persistence.SaveDataPersistence;

import java.io.File;
import java.io.IOException;

import kniffel.data.ScoreTable;

public class SaveGamesLoadRecyclerViewAdapter extends RecyclerView.Adapter<SaveGamesLoadRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private final SaveDataPersistence persistence;
    private String[] filenames;

    public SaveGamesLoadRecyclerViewAdapter(Context context, File path) throws IOException {
        this.context = context;
        persistence = new SaveDataPersistence(path);
        filenames = persistence.getListofSavedGames();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public SaveGamesLoadRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.save_game_list_item, parent, false);
        return new SaveGamesLoadRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SaveGamesLoadRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.saveName.setText(filenames[position]);


        //Handles setting a score at the end of your turn
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScoreTable scoreTable;
                String FILE_NOT_FOUND_MESSAGE = "Dateizugriff fehlgeschlagen.";
                String FILE_CORRUPTED_MESSAGE = "Diese Speicherdatei scheint beschädigt zu sein.";

                try {
                    scoreTable = persistence.loadGame(filenames[position]);
                    EngineStorage.scoreTable = scoreTable;
                    if(context instanceof Activity){
                        ((Activity)context).finish(); }
                    Intent intent = new Intent (v.getContext(), HostLoadedGameActivity.class);
                    v.getContext().startActivity(intent);
                } catch (IOException e) {
                    Toast.makeText(context, FILE_NOT_FOUND_MESSAGE, Toast.LENGTH_LONG).show();
                } catch (ClassNotFoundException e) {
                    Toast.makeText(context, FILE_CORRUPTED_MESSAGE, Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return filenames.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView saveName;
        RelativeLayout parentLayout;

        public ViewHolder(View view) {
            super(view);

            saveName = view.findViewById(R.id.save_name_text_view);
            parentLayout = view.findViewById(R.id.save_games_parent_layout);
        }

    }
}