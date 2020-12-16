package com.example.kniffel.GUI.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kniffel.R;

import java.io.IOException;

import kniffel.gamelogic.IllegalStateException;
import kniffel.gamelogic.KniffelException;
import kniffel.KniffelFacade;

public class ScoreTableRecyclerViewAdapter extends RecyclerView.Adapter<ScoreTableRecyclerViewAdapter.ViewHolder> {

    private final KniffelFacade facade;
    private Context context;

    public ScoreTableRecyclerViewAdapter(Context context, KniffelFacade facade) {
        this.context = context;
        this.facade = facade;
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.score_table_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        //Sets category names
        try {
            holder.categoryText.setText(facade.getScoreTableRowName(position));
        } catch (KniffelException e) {
            //Shouldn't be thrown.
            throw new RuntimeException();
        }

        //Sets scores
        for(int i = 1; i <= facade.getNumberOfPlayers(); i++) {
            try {
                int score = facade.getScore(position, i);
                if(score != -1) {
                    switch(i) {
                        case 1:
                            holder.playerOneScores.setText(String.valueOf(score));
                            break;
                        case 2:
                            holder.playerTwoScores.setText(String.valueOf(score));
                            break;
                        case 3:
                            holder.playerThreeScores.setText(String.valueOf(score));
                            break;
                        case 4:
                            holder.playerFourScores.setText(String.valueOf(score));
                            break;

                    }

                }
            } catch (KniffelException e) {
                //Shouldn't be thrown.
                throw new RuntimeException();
            }
        }

        //Handles setting a score at the end of your turn
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OPPONENT_TURN_MESSAGE = "Gegenspieler ist am Zug.";
                String PLAYER_SET_INVALID_ROW_MESSAGE = "Diese Zeile kann nicht gewählt werden.";
                String CONNECTION_LOST_MESSAGE = "Verbindungsfehler. Spiel muss beendet werden";

                try {
                    facade.endTurn(position);
                    Activity activity = (Activity) context;
                    activity.finish();
                } catch (IllegalStateException e) {
                    Toast.makeText(context, OPPONENT_TURN_MESSAGE, Toast.LENGTH_LONG).show();
                } catch (KniffelException e) {
                    Toast.makeText(context, PLAYER_SET_INVALID_ROW_MESSAGE, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(context, CONNECTION_LOST_MESSAGE, Toast.LENGTH_LONG).show();
                    //TODO: End game
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return facade.SCORE_TABLE_DIM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView categoryText;
        TextView playerOneScores;
        TextView playerTwoScores;
        TextView playerThreeScores;
        TextView playerFourScores;
        RelativeLayout parentLayout;

        public ViewHolder(View view) {
            super(view);

            categoryText = view.findViewById(R.id.categoryTextView);
            playerOneScores = view.findViewById(R.id.playerOneScoresTextView);
            playerTwoScores = view.findViewById(R.id.playerTwoScoresTextView);
            playerThreeScores = view.findViewById(R.id.playerThreeScoresTextView);
            playerFourScores = view.findViewById(R.id.playerFourScoresTextView);
            parentLayout = view.findViewById(R.id.score_table_parent_layout);

        }

    }
}