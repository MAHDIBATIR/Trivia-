package ma.enset.quizapp1;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginButton, topScoresButton;
    private TextView registerText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make status bar icons dark (for light status bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        
        // Disable reCAPTCHA verification for testing
//        mAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);

        // Initialize views with correct IDs from your XML
        emailInput = findViewById(R.id.Email);  // Note uppercase E in Email
        passwordInput = findViewById(R.id.Password);  // Note uppercase P in Password
        loginButton = findViewById(R.id.signInButton);
        registerText = findViewById(R.id.registerText);
        topScoresButton = findViewById(R.id.topScoresButton);
        progressBar = findViewById(R.id.progressBar);

        // Login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                
                // Validate inputs
                if (TextUtils.isEmpty(email)) {
                    emailInput.setError("Email is required");
                    return;
                }
                
                // Validate email format
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailInput.setError("Please enter a valid email address");
                    return;
                }
                
                if (TextUtils.isEmpty(password)) {
                    passwordInput.setError("Password is required");
                    return;
                }
                
                // Show progress bar
                progressBar.setVisibility(View.VISIBLE);
                
                // Authenticate with Firebase
                signIn(email, password);
            }
        });

        // Register text click listener
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to RegisterActivity
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
        
        // Top scores button click listener
        topScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to TopScoresActivity
                Intent topScoresIntent = new Intent(MainActivity.this, TopScoresActivity.class);
                startActivity(topScoresIntent);
            }
        });
    }
    
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, go directly to main menu
            startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
            finish();
        }
    }
    
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Login successful!", 
                                    Toast.LENGTH_SHORT).show();
                            
                            // Proceed to main menu
                            Intent mainMenuIntent = new Intent(MainActivity.this, MainMenuActivity.class);
                            startActivity(mainMenuIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user
                            Toast.makeText(MainActivity.this, "Authentication failed: " + 
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}