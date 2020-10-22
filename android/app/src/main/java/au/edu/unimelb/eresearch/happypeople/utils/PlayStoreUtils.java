package au.edu.unimelb.eresearch.happypeople.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import au.edu.unimelb.eresearch.happypeople.BuildConfig;

/**
 * Manages PlayStoreUtils related processes
 */
public class PlayStoreUtils {
    private static final String TAG = "PlayStoreUtils";

    /**
     * Checks if user should be shown review prompt.
     * Prompt only once, after app has done its 3rd successful prediction.
     * @param predictionsCount
     * @return
     */
    public static boolean shouldReview(int predictionsCount) {
        int reviewPromptsShown = SharedPref.getPref().getInt(SharedPref.REVIEW_PROMPTS_COUNT, 0);

        if (predictionsCount >= 3 && reviewPromptsShown < 1) {
            return true;
        }
        return false;
    }

    /**
     * Show review prompt to user
     * @param context
     * @param activity
     */
    public static void requestReview(Context context, Activity activity) {
        Log.i(TAG, "Review requested");
        ReviewManager manager = ReviewManagerFactory.create(context);
        Task<ReviewInfo> request = manager.requestReviewFlow();

        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);

                SharedPreferences sharedPref = SharedPref.getPref();
                SharedPreferences.Editor editor = sharedPref.edit();

                int reviewPromptsShown = sharedPref.getInt(SharedPref.REVIEW_PROMPTS_COUNT, 0) + 1;
                editor.putInt(SharedPref.REVIEW_PROMPTS_COUNT, reviewPromptsShown);
                editor.putString(SharedPref.LAST_RATING_VERSION, BuildConfig.VERSION_NAME);

                editor.commit();
            } else {
                Log.e(TAG, "Failed to request review");
            }
        });
    }
}
