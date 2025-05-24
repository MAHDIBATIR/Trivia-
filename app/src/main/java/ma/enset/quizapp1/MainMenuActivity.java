package ma.enset.quizapp1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {
    
    private Button startNewQuizButton;
    private Button continueQuizButton;
    private Button tryAgainButton;
    private Button topScoresButton;
    private ImageButton profileButton;
    private ImageButton logoutButton;
    private TextView welcomeText;
    
    private String userId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make status bar icons dark (for light status bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        
        setContentView(R.layout.activity_main_menu);
        
        // Initialize views
        startNewQuizButton = findViewById(R.id.startNewQuizButton);
        continueQuizButton = findViewById(R.id.continueQuizButton);
        tryAgainButton = findViewById(R.id.tryAgainButton);
        topScoresButton = findViewById(R.id.topScoresButton);
        profileButton = findViewById(R.id.profileButton);
        logoutButton = findViewById(R.id.logoutButton);
        welcomeText = findViewById(R.id.welcomeText);
        
        // Get current user
        if (FirebaseAuthHelper.isUserSignedIn()) {
            userId = FirebaseAuthHelper.getCurrentUser().getUid();
            String username = FirebaseAuthHelper.getUserDisplayName();
            welcomeText.setText("Welcome, " + username + "!");
        } else {
            // If not signed in, redirect to login
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        
        // Configure buttons based on user state
        configureButtonsForUserState();
        
        // Start New Quiz button
        startNewQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset quiz state
                QuizStateManager.resetQuizState(MainMenuActivity.this, userId);
                
                // Start quiz with non-randomized questions
                Intent intent = new Intent(MainMenuActivity.this, DynamicQuizActivity.class);
                intent.putExtra("randomizeQuestions", false);
                startActivity(intent);
            }
        });
        
        // Continue Quiz button
        continueQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start quiz activity - it will automatically continue from saved state
                Intent intent = new Intent(MainMenuActivity.this, DynamicQuizActivity.class);
                intent.putExtra("randomizeQuestions", false);
                startActivity(intent);
            }
        });
        
        // Try Again button (for returning users)
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start quiz with randomized questions
                Intent intent = new Intent(MainMenuActivity.this, DynamicQuizActivity.class);
                intent.putExtra("randomizeQuestions", true);
                startActivity(intent);
            }
        });
        
        // Top Scores button
        topScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, TopScoresActivity.class));
            }
        });
        
        // Profile button
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, ProfileActivity.class));
            }
        });
        
        // Logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuthHelper.signOut(MainMenuActivity.this, MainActivity.class);
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Configure buttons based on user state
        configureButtonsForUserState();
        
        // Get the quiz state information for debugging
        boolean hasStarted = QuizStateManager.hasStartedQuizzes(this, userId);
        int progress = QuizStateManager.getProgressPercentage(this, userId);
        int currentQuestion = QuizStateManager.getCurrentQuestionIndex(this, userId);
        int totalQuestions = QuizStateManager.getTotalQuestions(this);
        
        // Show debug info
        Toast.makeText(this, "Quiz State: Started=" + hasStarted + ", Progress=" + progress + 
                "%, Question=" + currentQuestion + "/" + totalQuestions, Toast.LENGTH_LONG).show();
    }
    
    /**
     * Configure buttons based on the user's quiz state:
     * - New user: Show "Start Quiz"
     * - Continuing user: Show "Continue Quiz"
     * - Returning user who completed quizzes: Show "Try Again"
     */
    private void configureButtonsForUserState() {
        // Make sure we have an accurate progress calculation
        int progress = QuizStateManager.getProgressPercentage(this, userId);
        if (progress < 0) progress = 0;
        if (progress > 100) progress = 100;
        
        // ALWAYS show all three buttons
        startNewQuizButton.setVisibility(View.VISIBLE);
        continueQuizButton.setVisibility(View.VISIBLE);
        tryAgainButton.setVisibility(View.VISIBLE);
        
        // Update the continue button text with progress
        continueQuizButton.setText(String.format("CONTINUE QUIZ (%d%%)", progress));
    }
} 