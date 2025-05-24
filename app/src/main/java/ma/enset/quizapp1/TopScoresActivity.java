package ma.enset.quizapp1;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TopScoresActivity extends AppCompatActivity {
    
    private ImageButton backButton;
    private ListView scoresList;
    private TextView noScoresText;
    private ProgressBar progressBar;
    private ScoreManager scoreManager;
    private static final int MAX_SCORES_TO_DISPLAY = 10;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make status bar icons dark (for light status bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        
        setContentView(R.layout.activity_top_scores);
        
        // Initialize views
        backButton = findViewById(R.id.backButton);
        scoresList = findViewById(R.id.scoresList);
        noScoresText = findViewById(R.id.noScoresText);
        progressBar = findViewById(R.id.progressBar);
        
        // Show progress bar while loading scores
        progressBar.setVisibility(View.VISIBLE);
        
        // Initialize score manager
        scoreManager = new ScoreManager(this);
        
        // Set up back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // Load and display top scores
        displayTopScores();
    }
    
    /**
     * Load and display top scores from Firebase Realtime Database
     */
    private void displayTopScores() {
        // Get scores from Firebase
        scoreManager.getTopScores(MAX_SCORES_TO_DISPLAY, new ScoreManager.FirebaseScoreListener() {
            @Override
            public void onScoresLoaded(List<ScoreManager.UserScore> scores) {
                // Hide progress bar
                progressBar.setVisibility(View.GONE);
                
                // Display the scores
                displayScores(scores);
            }

            @Override
            public void onError(String errorMessage) {
                // Log the error
                Log.e("TopScoresActivity", "Error loading scores: " + errorMessage);
                
                // Hide progress bar
                progressBar.setVisibility(View.GONE);
                
                // Show a toast with the error
                Toast.makeText(TopScoresActivity.this, 
                    "Error loading scores: " + errorMessage, 
                    Toast.LENGTH_SHORT).show();
                
                // Show error message in the UI
                noScoresText.setText("Error loading scores. Please try again later.");
                noScoresText.setVisibility(View.VISIBLE);
                scoresList.setVisibility(View.GONE);
            }
        });
    }
    
    /**
     * Display the scores in the ListView
     */
    private void displayScores(List<ScoreManager.UserScore> scores) {
        if (scores.isEmpty()) {
            // No scores available
            noScoresText.setVisibility(View.VISIBLE);
            scoresList.setVisibility(View.GONE);
        } else {
            // Format scores for display
            List<String> formattedScores = new ArrayList<>();
            for (int i = 0; i < scores.size(); i++) {
                ScoreManager.UserScore score = scores.get(i);
                formattedScores.add(String.format("%d. %s - %d points", 
                        i + 1, score.getUsername(), score.getScore()));
            }
            
            // Create adapter and set it to the list view
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    formattedScores
            );
            
            scoresList.setAdapter(adapter);
            noScoresText.setVisibility(View.GONE);
            scoresList.setVisibility(View.VISIBLE);
        }
    }
}