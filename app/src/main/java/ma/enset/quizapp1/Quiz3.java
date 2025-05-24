package ma.enset.quizapp1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Quiz3 extends AppCompatActivity {
    private RadioGroup optionsGroup;
    private Button nextButton;
    private int currentScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz3);

        // Initialize views
        optionsGroup = findViewById(R.id.radioGroup3);
        nextButton = findViewById(R.id.buttonNext3);

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
                    Toast.makeText(Quiz3.this, "Veuillez sélectionner une réponse", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check the answer (correct answer is Black Widow - answer3_1)
                if (selectedId == R.id.answer3_1) {
                    currentScore += 10; // Award points for correct answer
                    Toast.makeText(Quiz3.this, "Correct! Black Widow s'est sacrifiée pour la Pierre de l'Âme.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Quiz3.this, "Incorrect. La bonne réponse est Black Widow.", Toast.LENGTH_SHORT).show();
                }

                // Move to the next quiz (Quiz4) and pass the score
                Intent intent = new Intent(Quiz3.this, Quiz4.class);
                intent.putExtra("score", currentScore);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();

                // Optional: finish this activity if you don't want users to come back
                // finish();
            }
        });
    }
}