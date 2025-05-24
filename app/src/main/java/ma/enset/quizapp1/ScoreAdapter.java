package ma.enset.quizapp1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {
    private List<ScoreEntry> scores;

    public ScoreAdapter(List<ScoreEntry> scores) {
        this.scores = scores;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        ScoreEntry scoreEntry = scores.get(position);
        
        // Position is 0-indexed, but ranks should start at 1
        holder.rankTextView.setText(String.valueOf(position + 1));
        holder.nameTextView.setText(scoreEntry.getPlayerName());
        holder.scoreTextView.setText(String.valueOf(scoreEntry.getScore()));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public void updateScores(List<ScoreEntry> newScores) {
        this.scores = newScores;
        notifyDataSetChanged();
    }

    static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView, nameTextView, scoreTextView;

        ScoreViewHolder(View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.textViewRank);
            nameTextView = itemView.findViewById(R.id.textViewName);
            scoreTextView = itemView.findViewById(R.id.textViewScore);
        }
    }
} 