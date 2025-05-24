package ma.enset.quizapp1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Quiz5 extends AppCompatActivity {
    private RadioGroup optionsGroup;
    private Button nextButton;
    private int currentScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz5);

        // Initialize views
        optionsGroup = findViewById(R.id.radioGroup5);
        nextButton = findViewById(R.id.buttonNext5);

        // Get the score from previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentScore = extras.getInt("score", 0);
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if an option is selected
                int selectedId = optionsGroup.getCheckedRadioButtonId();

                if (selectedId == -1) {
                    Toast.makeText(Quiz5.this, "Veuillez sélectionner une réponse", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check the answer (correct answer is Leonardo DiCaprio - answer5_1)
                if (selectedId == R.id.answer5_1) {
                    currentScore += 10; // Award points for correct answer
                    Toast.makeText(Quiz5.this, "Correct! Leonardo DiCaprio a joué Jack dans Titanic.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Quiz5.this, "Incorrect. La bonne réponse est Leonardo DiCaprio.", Toast.LENGTH_SHORT).show();
                }

                // Move to Score activity with final score
                Intent intent = new Intent(Quiz5.this, Score.class);
                intent.putExtra("score", currentScore);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();

                // Finish this activity to prevent going back
                finish();
            }
        });
    }
}