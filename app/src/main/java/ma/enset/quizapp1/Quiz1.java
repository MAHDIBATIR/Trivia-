package ma.enset.quizapp1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Quiz1 extends AppCompatActivity {
    private TextView questionNumberText, questionText;
    private RadioGroup optionsGroup;
    private Button nextButton;
    private int currentScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz1);

        // Initialize views using your XML IDs
        questionNumberText = findViewById(R.id.textViewQuestionNumber1);
        questionText = findViewById(R.id.textViewQuestion1);
        optionsGroup = findViewById(R.id.radioGroup1);
        nextButton = findViewById(R.id.buttonNext1);

        // Set the question (already set in XML, but we can update it programmatically if needed)
        // questionText.setText("Qui a réalisé le film « Inception » ?");

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if an option is selected
                int selectedId = optionsGroup.getCheckedRadioButtonId();

                if (selectedId == -1) {
                    Toast.makeText(Quiz1.this, "Veuillez sélectionner une réponse", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check the answer (correct answer is Christopher Nolan - answer1_2)
                RadioButton selectedOption = findViewById(selectedId);

                if (selectedId == R.id.answer1_2) {
                    currentScore += 10; // Award points for correct answer
                    Toast.makeText(Quiz1.this, "Correct! Christopher Nolan a réalisé Inception.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Quiz1.this, "Incorrect. La bonne réponse est Christopher Nolan.", Toast.LENGTH_SHORT).show();
                }

                // Move to the next quiz (Quiz2) and pass the score
                Intent intent = new Intent(Quiz1.this, Quiz2.class);
                intent.putExtra("score", currentScore);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();

                // Optional: finish this activity if you don't want users to come back
                // finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Get the score from previous activity if exists
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentScore = extras.getInt("score", 0);
        }
    }
}