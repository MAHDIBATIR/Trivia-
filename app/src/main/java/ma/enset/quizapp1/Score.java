package ma.enset.quizapp1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Score extends AppCompatActivity {

    private ProgressBar progressBackground;
    private ProgressBar progressBar;
    private TextView percentageText;
    private Button logoutButton;
    private Button tryAgainButton;
    private Button viewTopScoresButton;
    private Button mainMenuButton;
    private ScoreManager scoreManager;
    private int finalScore;
    private int totalQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make status bar icons dark (for light status bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        
        setContentView(R.layout.activity_score);

        // Initialize UI elements
        progressBackground = findViewById(R.id.progressBackground);
        progressBar = findViewById(R.id.pb);
        percentageText = findViewById(R.id.tv2);
        logoutButton = findViewById(R.id.lo);
        tryAgainButton = findViewById(R.id.ta);
        viewTopScoresButton = findViewById(R.id.viewTopScores);
        mainMenuButton = findViewById(R.id.mainMenuButton);
        
        // Initialize ScoreManager
        scoreManager = new ScoreManager(this);

        // Get the score from intent
        finalScore = getIntent().getIntExtra("score", 0);
        totalQuestions = getIntent().getIntExtra("totalQuestions", 5);
        
        // Calculate percentage (default: 5 questions x 10 points each = 50 max points)
        int maxScore = totalQuestions * 10;
        int percentage = (finalScore * 100) / maxScore;

        // Update progress bar and percentage text
        progressBar.setProgress(percentage);
        percentageText.setText(percentage + "%");
        
        // Display username if available from Firebase
        if (FirebaseAuthHelper.isUserSignedIn()) {
            TextView tv1 = findViewById(R.id.tv1);
            String username = FirebaseAuthHelper.getUserDisplayName();
            tv1.setText(username + ", your score is:");
        }

        // Logout button logic
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out from Firebase
                FirebaseAuthHelper.signOut(Score.this, MainActivity.class);
            }
        });

        // Try Again button logic
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset quiz state for this user
                String userId = FirebaseAuthHelper.getCurrentUser().getUid();
                QuizStateManager.resetQuizState(Score.this, userId);
                
                // Start the quiz from the beginning with randomized questions
                Intent intent = new Intent(Score.this, DynamicQuizActivity.class);
                intent.putExtra("randomizeQuestions", true);
                startActivity(intent);
                finish();
            }
        });
        
        // View Top Scores button
        viewTopScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Score.this, TopScoresActivity.class);
                startActivity(intent);
            }
        });
        
        // Main Menu button
        mainMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Score.this, MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    // Prevent going back to the last quiz question with back button
    @Override
    public void onBackPressed() {
        // Navigate to MainActivity instead of going back to quiz
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        
        // Still call super as required by lint
        super.onBackPressed();
    }
}