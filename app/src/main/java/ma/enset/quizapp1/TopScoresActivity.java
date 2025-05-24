package ma.enset.quizapp1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TopScoresActivity extends AppCompatActivity {
    private RecyclerView recyclerViewTopScores;
    private TextView textViewNoScores;
    private Button buttonReturnToMain;
    private ScoreAdapter scoreAdapter;
    private ScoreManager scoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_scores);

        // Initialize views
        recyclerViewTopScores = findViewById(R.id.recyclerViewTopScores);
        textViewNoScores = findViewById(R.id.textViewNoScores);
        buttonReturnToMain = findViewById(R.id.buttonReturnToMain);
        
        // Initialize ScoreManager
        scoreManager = new ScoreManager(this);
        
        // Set up RecyclerView
        recyclerViewTopScores.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTopScores.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        
        // Load and display top scores
        loadTopScores();
        
        // Set up button click listener
        buttonReturnToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to main menu
                Intent intent = new Intent(TopScoresActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the activity stack
                startActivity(intent);
                finish();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh scores when resuming the activity
        loadTopScores();
    }
    
    private void loadTopScores() {
        List<ScoreEntry> topScores = scoreManager.getTopScores();
        
        if (topScores.isEmpty()) {
            textViewNoScores.setVisibility(View.VISIBLE);
            recyclerViewTopScores.setVisibility(View.GONE);
        } else {
            textViewNoScores.setVisibility(View.GONE);
            recyclerViewTopScores.setVisibility(View.VISIBLE);
            
            // Set up or update the adapter
            if (scoreAdapter == null) {
                scoreAdapter = new ScoreAdapter(topScores);
                recyclerViewTopScores.setAdapter(scoreAdapter);
            } else {
                scoreAdapter.updateScores(topScores);
            }
        }
    }
} 