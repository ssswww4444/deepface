package au.edu.unimelb.eresearch.happypeople.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Singleton instance of SharedPreference
 */
public class SharedPref {
    public static final String SHARED_PREF_NAME = "HappyPeople Shared Preference";

    // Shared Preference keys
    public static final String LAST_RATING_VERSION = "LAST_RATING_VERSION";
    public static final String PREDICTIONS_COUNT = "PREDICTIONS_COUNT";
    public static final String REVIEW_PROMPTS_COUNT = "REVIEW_PROMPTS_COUNT";

    private static Context context;
    private static SharedPreferences pref;

    public static void initialize(Context context) {
        try {
            context = context;
            pref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SharedPreferences getPref() {
        return pref;
    }
}
