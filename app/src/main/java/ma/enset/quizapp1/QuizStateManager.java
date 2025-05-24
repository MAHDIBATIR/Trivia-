package ma.enset.quizapp1;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages the state of quizzes for users, including tracking progress and completion status.
 */
public class QuizStateManager {
    private static final String PREFS_NAME = "quiz_state_prefs";
    private static final String KEY_QUIZ_STARTED = "quiz_started_";
    private static final String KEY_QUIZ_COMPLETED = "quiz_completed_";
    private static final String KEY_CURRENT_QUESTION = "current_question_";
    private static final String KEY_TOTAL_QUESTIONS = "total_questions";
    
    /**
     * Checks if a user has started any quizzes
     * @param context Application context
     * @param userId User ID to check
     * @return true if quizzes have been started, false otherwise
     */
    public static boolean hasStartedQuizzes(Context context, String userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_QUIZ_STARTED + userId, false);
    }
    
    /**
     * Marks quizzes as started for a user
     * @param context Application context
     * @param userId User ID to update
     */
    public static void markQuizzesAsStarted(Context context, String userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_QUIZ_STARTED + userId, true);
        editor.apply();
    }
    
    /**
     * Checks if a user has completed all quizzes
     * @param context Application context
     * @param userId User ID to check
     * @return true if all quizzes are completed, false otherwise
     */
    public static boolean hasCompletedQuizzes(Context context, String userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_QUIZ_COMPLETED + userId, false);
    }
    
    /**
     * Marks quizzes as completed for a user
     * @param context Application context
     * @param userId User ID to update
     */
    public static void markQuizzesAsCompleted(Context context, String userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_QUIZ_COMPLETED + userId, true);
        editor.apply();
    }
    
    /**
     * Gets the current question index for a user
     * @param context Application context
     * @param userId User ID to check
     * @return The current question index (0-based)
     */
    public static int getCurrentQuestionIndex(Context context, String userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_CURRENT_QUESTION + userId, 0);
    }
    
    /**
     * Updates the current question index for a user
     * @param context Application context
     * @param userId User ID to update
     * @param questionIndex The current question index
     */
    public static void updateCurrentQuestionIndex(Context context, String userId, int questionIndex) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CURRENT_QUESTION + userId, questionIndex);
        editor.apply();
    }
    
    /**
     * Saves the total number of questions in the quiz
     * @param context Application context
     * @param totalQuestions Total number of questions
     */
    public static void saveTotalQuestions(Context context, int totalQuestions) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_TOTAL_QUESTIONS, totalQuestions);
        editor.apply();
    }
    
    /**
     * Gets the total number of questions in the quiz
     * @param context Application context
     * @return The total number of questions
     */
    public static int getTotalQuestions(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_TOTAL_QUESTIONS, 0);
    }
    
    /**
     * Gets the user's progress percentage
     * @param context Application context
     * @param userId User ID to check
     * @return Progress as a percentage (0-100)
     */
    public static int getProgressPercentage(Context context, String userId) {
        int currentQuestion = getCurrentQuestionIndex(context, userId);
        int totalQuestions = getTotalQuestions(context);
        
        if (totalQuestions == 0) {
            return 0;
        }
        
        return (currentQuestion * 100) / totalQuestions;
    }
    
    /**
     * Resets the quiz state for a user
     * @param context Application context
     * @param userId User ID to reset
     */
    public static void resetQuizState(Context context, String userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_QUIZ_STARTED + userId, false);
        editor.putBoolean(KEY_QUIZ_COMPLETED + userId, false);
        editor.putInt(KEY_CURRENT_QUESTION + userId, 0);
        editor.apply();
    }
} 