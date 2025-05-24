package ma.enset.quizapp1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Score extends AppCompatActivity {

    private ProgressBar progressBackground;
    private ProgressBar progressBar;
    private TextView percentageText;
    private Button logoutButton;
    private Button tryAgainButton;
    private Button viewTopScoresButton;
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
        
        // Check if this is a high score
        if (scoreManager.isHighScore(finalScore)) {
            showSaveScoreDialog();
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
                // Start the quiz from the beginning
                Intent intent = new Intent(Score.this, DynamicQuizActivity.class);
                intent.putExtra("score", 0); // Reset score to 0
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
    }
    
    private void showSaveScoreDialog() {
        // Create dialog with name input for high score
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New High Score!");
        builder.setMessage("Congratulations! You got " + finalScore + " points. Enter your name:");
        
        // Inflate custom layout for the dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_save_score, null);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        
        // Get references to views in the dialog
        EditText nameInput = dialogView.findViewById(R.id.editTextName);
        Button saveButton = dialogView.findViewById(R.id.buttonSave);
        Button cancelButton = dialogView.findViewById(R.id.buttonCancel);
        
        // Set default name to Firebase display name if available
        if (FirebaseAuthHelper.isUserSignedIn()) {
            nameInput.setText(FirebaseAuthHelper.getUserDisplayName());
        }
        
        // Set button listeners
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerName = nameInput.getText().toString().trim();
                if (TextUtils.isEmpty(playerName)) {
                    nameInput.setError("Please enter your name");
                    return;
                }
                
                // Save the score
                scoreManager.addScore(playerName, finalScore);
                Toast.makeText(Score.this, "Score saved!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        dialog.show();
    }

    // Prevent going back to the last quiz question with back button
    @Override
    public void onBackPressed() {
        // Navigate to MainActivity instead of going back to quiz
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        
        // Still call super as required by lint
        super.onBackPressed();
    }
}