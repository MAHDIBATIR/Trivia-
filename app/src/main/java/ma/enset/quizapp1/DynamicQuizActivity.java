package ma.enset.quizapp1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DynamicQuizActivity extends AppCompatActivity {
    private TextView questionNumberText, questionText, currentScoreText, timeLeftText;
    private RadioGroup optionsGroup;
    private Button nextButton;
    private ImageButton profileButton;
    private ImageView questionImage;
    private ProgressBar timerProgressBar;
    
    private int currentScore = 0;
    private int currentQuestionIndex = 0;
    private List<Question> questions;
    
    // Timer variables
    private static final long QUESTION_TIMER_DURATION = 30000; // 30 seconds
    private static final long TIMER_INTERVAL = 100; // Update timer every 0.1 seconds
    private CountDownTimer questionTimer;
    private long timeLeftInMillis = QUESTION_TIMER_DURATION;
    private boolean isTimerRunning = false;
    private boolean isProcessingAnswer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make status bar icons dark (for light status bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        
        setContentView(R.layout.activity_dynamic_quiz);
        
        // Initialize views
        questionNumberText = findViewById(R.id.textViewQuestionNumber);
        questionText = findViewById(R.id.textViewQuestion);
        optionsGroup = findViewById(R.id.radioGroup);
        nextButton = findViewById(R.id.buttonNext);
        questionImage = findViewById(R.id.questionImage);
        currentScoreText = findViewById(R.id.textViewCurrentScore);
        timeLeftText = findViewById(R.id.textViewTimeLeft);
        timerProgressBar = findViewById(R.id.timerProgressBar);
        profileButton = findViewById(R.id.profileButton);

        // Load questions from data source
        loadQuestionsFromJson();

        // Display the first question
        displayQuestion(currentQuestionIndex);
        
        // Initialize score display
        updateScoreDisplay();
        
        // Start timer for the current question
        startQuestionTimer();

        // Set up next button click listener
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processAnswer();
            }
        });
        
        // Set up profile button click listener
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to profile activity
                Intent intent = new Intent(DynamicQuizActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
    
    private void processAnswer() {
        // Prevent multiple simultaneous calls
        if (isProcessingAnswer) {
            return;
        }
        
        isProcessingAnswer = true;
        
        // Cancel current timer
        if (questionTimer != null) {
            questionTimer.cancel();
            isTimerRunning = false;
        }

        // Check if an option is selected
        int selectedId = optionsGroup.getCheckedRadioButtonId();

        // Get the current question
        Question currentQuestion = questions.get(currentQuestionIndex);
        
        if (selectedId != -1) {
            // Check the answer
            RadioButton selectedRadioButton = findViewById(selectedId);
            if (selectedRadioButton != null) {
                String selectedAnswer = selectedRadioButton.getText().toString();
                
                if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
                    currentScore += 10; // Award points for correct answer
                    Toast.makeText(DynamicQuizActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
                    
                    // Update score display
                    updateScoreDisplay();
                } else {
                    Toast.makeText(DynamicQuizActivity.this, 
                        "Incorrect. The correct answer is: " + currentQuestion.getCorrectAnswer(), 
                        Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            // No answer selected - inform user they ran out of time or skipped
            Toast.makeText(DynamicQuizActivity.this, 
                "Time's up! The correct answer was: " + currentQuestion.getCorrectAnswer(), 
                Toast.LENGTH_SHORT).show();
        }

        // Move to next question or finish quiz
        moveToNextQuestion();
        
        // Reset processing flag
        isProcessingAnswer = false;
    }
    
    private void moveToNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            displayQuestion(currentQuestionIndex);
            // Reset and restart timer for the next question
            timeLeftInMillis = QUESTION_TIMER_DURATION;
            startQuestionTimer();
        } else {
            // Quiz completed - go to results
            Intent intent = new Intent(DynamicQuizActivity.this, Score.class);
            intent.putExtra("score", currentScore);
            intent.putExtra("totalQuestions", questions.size());
            startActivity(intent);
            finish();
        }
    }
    
    private void updateScoreDisplay() {
        currentScoreText.setText(String.valueOf(currentScore));
    }
    
    private void startQuestionTimer() {
        // Cancel any existing timer first
        if (questionTimer != null) {
            questionTimer.cancel();
            isTimerRunning = false;
        }
        
        // Reset timer progress bar
        timerProgressBar.setMax((int) QUESTION_TIMER_DURATION);
        timerProgressBar.setProgress((int) timeLeftInMillis);
        
        // Reset timer text
        timeLeftText.setTextColor(getResources().getColor(android.R.color.black));
        
        isTimerRunning = true;
        questionTimer = new CountDownTimer(timeLeftInMillis, TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!isTimerRunning) {
                    return;
                }
                
                timeLeftInMillis = millisUntilFinished;
                int secondsLeft = (int) (millisUntilFinished / 1000);
                
                // Update timer text
                timeLeftText.setText("Time: " + secondsLeft + "s");
                
                // Update progress bar
                timerProgressBar.setProgress((int) millisUntilFinished);
                
                // Change color when time is running out (less than 5 seconds)
                if (secondsLeft <= 5) {
                    timeLeftText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    timeLeftText.setTextColor(getResources().getColor(android.R.color.black));
                }
            }

            @Override
            public void onFinish() {
                if (!isTimerRunning) {
                    return;
                }
                
                isTimerRunning = false;
                timeLeftText.setText("Time's up!");
                timeLeftInMillis = 0;
                timerProgressBar.setProgress(0);
                
                // Auto-process the answer when time is up
                processAnswer();
            }
        }.start();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Cancel timer if activity is paused
        if (questionTimer != null) {
            questionTimer.cancel();
            isTimerRunning = false;
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Resume timer if not finished and not already running
        if (currentQuestionIndex < questions.size() && timeLeftInMillis > 0 && !isTimerRunning) {
            startQuestionTimer();
        }
    }

    private void loadQuestionsFromJson() {
        questions = new ArrayList<>();
        
        try {
            // Read the JSON file from assets
            String jsonString = readJsonFromAsset("questions.json");
            
            // Parse the JSON
            JSONArray jsonArray = new JSONArray(jsonString);
            
            // Loop through all questions in the JSON
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject questionObj = jsonArray.getJSONObject(i);
                
                // Get question text
                String questionText = questionObj.getString("questionText");
                
                // Get options
                JSONArray optionsArray = questionObj.getJSONArray("options");
                List<String> options = new ArrayList<>();
                for (int j = 0; j < optionsArray.length(); j++) {
                    options.add(optionsArray.getString(j));
                }
                
                // Get correct answer
                String correctAnswer = questionObj.getString("correctAnswer");
                
                // Get image resource if exists
                Integer imageResource = null;
                if (!questionObj.isNull("imageResource") && 
                    questionObj.getString("imageResource") != null && 
                    !questionObj.getString("imageResource").equals("null")) {
                    imageResource = getResources().getIdentifier(
                        questionObj.getString("imageResource"), 
                        "drawable", 
                        getPackageName());
                }
                
                // Create question object
                Question question = new Question(questionText, options, correctAnswer, imageResource);
                
                // Add to list
                questions.add(question);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            // If JSON fails to load, fall back to hardcoded questions
            loadHardcodedQuestions();
            Toast.makeText(this, "Failed to load questions from JSON, using defaults", Toast.LENGTH_LONG).show();
        }
        
        // If no questions were loaded (empty JSON file), fall back to hardcoded questions
        if (questions.isEmpty()) {
            loadHardcodedQuestions();
        }
    }
    
    private String readJsonFromAsset(String filename) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream is = getAssets().open(filename);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    private void loadHardcodedQuestions() {
        // Initialize the questions list if not already
        if (questions == null) {
            questions = new ArrayList<>();
        }

        // Add hardcoded sample questions as fallback
        // Question 1
        List<String> options1 = new ArrayList<>();
        options1.add("Steven Spielberg");
        options1.add("Christopher Nolan");
        options1.add("James Cameron");
        options1.add("Martin Scorsese");
        questions.add(new Question(
                "Who directed the film 'Inception'?",
                options1,
                "Christopher Nolan",
                null)); // No image for this question

        // Question 2
        List<String> options2 = new ArrayList<>();
        options2.add("Breaking Bad");
        options2.add("Game of Thrones");
        options2.add("The Walking Dead");
        options2.add("The Sopranos");
        questions.add(new Question(
                "Walter White is the main character in which TV series?",
                options2,
                "Breaking Bad",
                null));

        // Question 3
        List<String> options3 = new ArrayList<>();
        options3.add("Michael Keaton");
        options3.add("Christian Bale");
        options3.add("Ben Affleck");
        options3.add("Robert Pattinson");
        questions.add(new Question(
                "Who played Batman in 'The Dark Knight' trilogy?",
                options3,
                "Christian Bale",
                null));

        // Question 4
        List<String> options4 = new ArrayList<>();
        options4.add("The Avengers");
        options4.add("Justice League");
        options4.add("Guardians of the Galaxy");
        options4.add("The Eternals");
        questions.add(new Question(
                "Which superhero team includes Iron Man, Thor, and Captain America?",
                options4,
                "The Avengers",
                null));

        // Question 5
        List<String> options5 = new ArrayList<>();
        options5.add("Joe Russo");
        options5.add("James Gunn");
        options5.add("Peter Jackson");
        options5.add("George Lucas");
        questions.add(new Question(
                "Who directed the 'Lord of the Rings' trilogy?",
                options5,
                "Peter Jackson",
                null));
    }

    private void displayQuestion(int index) {
        // Clear previous radio buttons
        optionsGroup.removeAllViews();
        optionsGroup.clearCheck();

        // Get current question
        Question question = questions.get(index);

        // Set question text
        questionText.setText(question.getQuestionText());

        // Set question number text
        questionNumberText.setText("Question " + (index + 1) + "/" + questions.size());

        // Set question image if available
        if (question.getImageResource() != null) {
            questionImage.setImageResource(question.getImageResource());
            questionImage.setVisibility(View.VISIBLE);
        } else {
            questionImage.setVisibility(View.GONE);
        }

        // Add radio buttons for options
        for (String option : question.getOptions()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(option);
            radioButton.setTextColor(getResources().getColor(android.R.color.white));
            radioButton.setTextSize(16);
            // Add padding to make it more touchable
            radioButton.setPadding(0, 16, 0, 16);
            optionsGroup.addView(radioButton);
        }
        
        // Reset timer text and progress bar
        timeLeftText.setText("Time: 30s");
        timerProgressBar.setProgress((int) QUESTION_TIMER_DURATION);
        timeLeftInMillis = QUESTION_TIMER_DURATION;
    }
} 