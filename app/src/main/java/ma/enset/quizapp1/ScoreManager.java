package ma.enset.quizapp1;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreManager {
    private static final String PREF_NAME = "quiz_scores";
    private static final String SCORES_KEY = "top_scores";
    private static final int MAX_SCORES = 5;

    private SharedPreferences preferences;
    private Gson gson;

    public ScoreManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public List<ScoreEntry> getTopScores() {
        String scoresJson = preferences.getString(SCORES_KEY, null);
        List<ScoreEntry> scores;

        if (scoresJson == null) {
            scores = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<ScoreEntry>>() {}.getType();
            scores = gson.fromJson(scoresJson, type);
        }

        // Sort scores in descending order
        Collections.sort(scores);
        return scores;
    }

    public boolean isHighScore(int score) {
        List<ScoreEntry> scores = getTopScores();
        
        // If we have fewer than MAX_SCORES, any score is a high score
        if (scores.size() < MAX_SCORES) {
            return true;
        }
        
        // Check if the new score is higher than the lowest score in the list
        return score > scores.get(scores.size() - 1).getScore();
    }

    public void addScore(String playerName, int score) {
        List<ScoreEntry> scores = getTopScores();
        scores.add(new ScoreEntry(playerName, score));
        
        // Sort scores in descending order
        Collections.sort(scores);
        
        // Keep only the top MAX_SCORES
        if (scores.size() > MAX_SCORES) {
            scores = scores.subList(0, MAX_SCORES);
        }
        
        // Save the updated scores
        saveScores(scores);
    }

    private void saveScores(List<ScoreEntry> scores) {
        String scoresJson = gson.toJson(scores);
        preferences.edit().putString(SCORES_KEY, scoresJson).apply();
    }

    public void clearScores() {
        preferences.edit().remove(SCORES_KEY).apply();
    }
} 