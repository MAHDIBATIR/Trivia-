package ma.enset.quizapp1;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Helper class to manage Firebase Authentication tasks
 */
public class FirebaseAuthHelper {
    
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    
    /**
     * Check if a user is currently signed in
     * @return true if a user is signed in, false otherwise
     */
    public static boolean isUserSignedIn() {
        return mAuth.getCurrentUser() != null;
    }
    
    /**
     * Get the current signed in user
     * @return FirebaseUser object for the current user or null if no user is signed in
     */
    public static FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
    
    /**
     * Get the display name of the current user
     * @return Display name string or "Guest" if not available
     */
    public static String getUserDisplayName() {
        FirebaseUser user = getCurrentUser();
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            return user.getDisplayName();
        }
        return "Guest";
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
     * Sign the current user out
     * @param context Context to use for operations
     * @param targetActivity Activity class to navigate to after signing out (optional)
     */
    public static void signOut(Context context, Class<?> targetActivity) {
        mAuth.signOut();
        
        if (targetActivity != null) {
            Intent intent = new Intent(context, targetActivity);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
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