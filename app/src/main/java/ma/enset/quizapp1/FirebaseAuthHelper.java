package ma.enset.quizapp1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Helper class for Firebase Authentication operations
 */
public class FirebaseAuthHelper {
    
    /**
     * Check if a user is signed in
     * @return true if user is signed in, false otherwise
     */
    public static boolean isUserSignedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }
    
    /**
     * Get the currently signed in Firebase user
     * @return FirebaseUser object or null if no user is signed in
     */
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }
    
    /**
     * Get the display name of the current user
     * @return Display name or "User" if not available
     */
    public static String getUserDisplayName() {
        FirebaseUser user = getCurrentUser();
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            return user.getDisplayName();
        }
        return "User";
    }
    
    /**
     * Get the email address of the current user
     * @return Email address string or null if not available
     */
    public static String getUserEmail() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            return user.getEmail();
        }
        return null;
    }
    
    /**
     * Sign out the current user and redirect to another activity
     * @param context Current activity context
     * @param destClass Destination activity class after logout
     */
    public static void signOut(Context context, Class<?> destClass) {
        // Sign out of Firebase
        FirebaseAuth.getInstance().signOut();
        
        // Show toast message
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        // Redirect to destination activity
        Intent intent = new Intent(context, destClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        
        // If context is an activity, finish it
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }
    
    /**
     * Delete the current user account
     * @param context Context to use for showing Toast messages
     * @param onCompleteListener Listener to be called when operation completes (can be null)
     */
    public static void deleteAccount(final Context context, OnCompleteListener<Void> onCompleteListener) {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                if (onCompleteListener != null) {
                                    onCompleteListener.onComplete(task);
                                }
                            } else {
                                Toast.makeText(context, "Failed to delete account: " + task.getException().getMessage(), 
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
} 