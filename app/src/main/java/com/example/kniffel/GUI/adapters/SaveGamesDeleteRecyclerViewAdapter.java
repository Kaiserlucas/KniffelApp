package com.example.kniffel.GUI.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kniffel.R;
import com.example.kniffel.persistence.SaveDataPersistence;

import java.io.File;
import java.io.IOException;

public class SaveGamesDeleteRecyclerViewAdapter extends RecyclerView.Adapter<SaveGamesDeleteRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private final SaveDataPersistence persistence;
    private String[] filenames;

    public SaveGamesDeleteRecyclerViewAdapter(Context context, File path) throws IOException {
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
    public SaveGamesDeleteRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.save_game_list_item, parent, false);
        return new SaveGamesDeleteRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SaveGamesDeleteRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.saveName.setText(filenames[position]);


        //Handles setting a score at the end of your turn
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String FILE_NOT_FOUND_MESSAGE = "Dateizugriff fehlgeschlagen.";
                String FILE_DELETED_MESSAGE = "Der Spielstand wurde gelöscht.";

                try {
                    if(persistence.deleteGame(filenames[position])) {
                        Toast.makeText(context, FILE_DELETED_MESSAGE, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, FILE_NOT_FOUND_MESSAGE, Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(context, FILE_NOT_FOUND_MESSAGE, Toast.LENGTH_LONG).show();
                }

                try {
                    filenames = persistence.getListofSavedGames();
                } catch (IOException e) {
                    Toast.makeText(context, FILE_NOT_FOUND_MESSAGE, Toast.LENGTH_LONG).show();
                }
                notifyDataSetChanged();
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
