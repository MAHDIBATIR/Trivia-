package ma.enset.quizapp1;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Manages scores for users in the quiz app.
 * Handles storing, retrieving, and updating user scores in Firebase Realtime Database.
 */
public class ScoreManager {
    private Context context;
    private DatabaseReference databaseRef;
    private static final String FIREBASE_DB_URL = "https://quizapp-b7781-default-rtdb.firebaseio.com/";
    
    public ScoreManager(Context context) {
        this.context = context;
        // Initialize Firebase Database with explicit URL
        FirebaseDatabase database = FirebaseDatabase.getInstance(FIREBASE_DB_URL);
        this.databaseRef = database.getReference("scores");
    }

    /**
     * Saves a user's score to Firebase Realtime Database
     *
     * @param userId The Firebase user ID
     * @param username The display name of the user
     * @param score The score to save
     * @return true to indicate operation started (result is async)
     */
    public boolean saveUserScore(String userId, String username, int score) {
        // Save to Firebase Realtime Database
        saveScoreToFirebase(userId, username, score);
        return true;
    }

    /**
     * Saves a score to Firebase Realtime Database
     *
     * @param userId The Firebase user ID
     * @param username The display name of the user
     * @param score The score to save
     */
    private void saveScoreToFirebase(String userId, String username, int score) {
        try {
            // Use the class database reference instead of creating a new one
            DatabaseReference scoresRef = this.databaseRef;

            // Create unique score ID
            String scoreId = scoresRef.push().getKey();

            // Format current date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            // Create score object
            Map<String, Object> scoreData = new HashMap<>();
            scoreData.put("userId", userId);
            scoreData.put("username", username);
            scoreData.put("score", score);
            scoreData.put("date", currentDate);

            // Save to Firebase
            if (scoreId != null) {
                scoresRef.child(scoreId).setValue(scoreData)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("ScoreManager", "Score saved to Firebase successfully");
                                } else {
                                    Log.e("ScoreManager", "Failed to save score to Firebase", task.getException());
                                }
                            }
                        });
            }
        } catch (Exception e) {
            Log.e("ScoreManager", "Error saving score to Firebase", e);
        }
    }

    /**
     * Gets the top score for a specific user from Firebase
     *
     * @param userId The Firebase user ID
     * @param listener Listener to be called when top score is retrieved
     */
    public void getUserTopScore(String userId, final UserTopScoreListener listener) {
        // Use the class database reference
        DatabaseReference scoresRef = this.databaseRef;
        
        scoresRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int topScore = 0;
                
                for (DataSnapshot scoreSnapshot : dataSnapshot.getChildren()) {
                    Integer score = scoreSnapshot.child("score").getValue(Integer.class);
                    if (score != null && score > topScore) {
                        topScore = score;
                    }
                }
                
                listener.onTopScoreRetrieved(topScore);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ScoreManager", "Error getting top score", databaseError.toException());
                listener.onError(databaseError.getMessage());
            }
        });
    }
    
    /**
     * Interface for top score retrieval callbacks
     */
    public interface UserTopScoreListener {
        void onTopScoreRetrieved(int topScore);
        void onError(String errorMessage);
    }

    /**
     * Get all scores for a specific user from Firebase
     *
     * @param userId The Firebase user ID
     * @param listener Listener to be called when scores are retrieved
     */
    public void getUserScores(String userId, final UserScoresListener listener) {
        // Use the class database reference
        DatabaseReference scoresRef = this.databaseRef;
        
        scoresRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Integer> scores = new ArrayList<>();
                
                for (DataSnapshot scoreSnapshot : dataSnapshot.getChildren()) {
                    Integer score = scoreSnapshot.child("score").getValue(Integer.class);
                    if (score != null) {
                        scores.add(score);
                    }
                }
                
                // Sort scores in descending order
                Collections.sort(scores, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer score1, Integer score2) {
                        return score2.compareTo(score1);
                    }
                });
                
                listener.onScoresRetrieved(scores);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ScoreManager", "Error getting user scores", databaseError.toException());
                listener.onError(databaseError.getMessage());
            }
        });
    }
    
    /**
     * Interface for user scores retrieval callbacks
     */
    public interface UserScoresListener {
        void onScoresRetrieved(List<Integer> scores);
        void onError(String errorMessage);
    }



    /**
     * Get top scores from Firebase Realtime Database
     *
     * @param limit Maximum number of scores to return
     * @param listener Listener to be called when scores are retrieved
     */
    public void getTopScores(final int limit, final FirebaseScoreListener listener) {
        try {
            // Use the class database reference
            DatabaseReference scoresRef = this.databaseRef;

            // Query to get all scores (we'll filter and sort them later)
            scoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<UserScore> allScores = new ArrayList<>();
                    Map<String, Integer> highestScoreByUser = new HashMap<>();
                    Map<String, String> dateByUser = new HashMap<>();

                    // Process all scores
                    for (DataSnapshot scoreSnapshot : dataSnapshot.getChildren()) {
                        try {
                            String userId = scoreSnapshot.child("userId").getValue(String.class);
                            String username = scoreSnapshot.child("username").getValue(String.class);
                            Integer score = scoreSnapshot.child("score").getValue(Integer.class);
                            String date = scoreSnapshot.child("date").getValue(String.class);

                            if (userId != null && username != null && score != null) {
                                // Keep track of highest score per user
                                if (!highestScoreByUser.containsKey(userId) || score > highestScoreByUser.get(userId)) {
                                    highestScoreByUser.put(userId, score);
                                    dateByUser.put(userId, date != null ? date : "Unknown");
                                }
                            }
                        } catch (Exception e) {
                            Log.e("ScoreManager", "Error processing score", e);
                        }
                    }

                    // Create UserScore objects from highest scores
                    for (Map.Entry<String, Integer> entry : highestScoreByUser.entrySet()) {
                        String userId = entry.getKey();
                        Integer score = entry.getValue();
                        String date = dateByUser.get(userId);

                        // Find username for this userId
                        String username = "Unknown";
                        for (DataSnapshot scoreSnapshot : dataSnapshot.getChildren()) {
                            String uid = scoreSnapshot.child("userId").getValue(String.class);
                            if (uid != null && uid.equals(userId)) {
                                String name = scoreSnapshot.child("username").getValue(String.class);
                                if (name != null) {
                                    username = name;
                                    break;
                                }
                            }
                        }

                        allScores.add(new UserScore(username, score, date));
                    }

                    // Sort scores in descending order
                    Collections.sort(allScores, new Comparator<UserScore>() {
                        @Override
                        public int compare(UserScore score1, UserScore score2) {
                            return Integer.compare(score2.getScore(), score1.getScore());
                        }
                    });

                    // Limit the number of scores
                    List<UserScore> topScores = allScores.size() <= limit ?
                            allScores : allScores.subList(0, limit);

                    // Notify listener
                    listener.onScoresLoaded(topScores);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ScoreManager", "Error loading scores from Firebase", databaseError.toException());
                    listener.onError(databaseError.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ScoreManager", "Error getting scores from Firebase", e);
            listener.onError(e.getMessage());
        }
    }

    /**
     * Interface for Firebase score loading callbacks
     */
    public interface FirebaseScoreListener {
        void onScoresLoaded(List<UserScore> scores);
        void onError(String errorMessage);
    }

    /**
     * Class to represent a user's score with username and date
     */
    public static class UserScore {
        private String username;
        private int score;
        private String date;

        public UserScore() {
            // Default constructor required for Firebase
        }

        public UserScore(String username, int score, String date) {
            this.username = username;
            this.score = score;
            this.date = date;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}