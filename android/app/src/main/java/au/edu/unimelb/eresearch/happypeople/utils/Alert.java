package au.edu.unimelb.eresearch.happypeople.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Toast;

public class Alert {
    public static void alert(Activity activity, String title, String message, String ok) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(ok, (dialogInterface, i) -> {});
        activity.runOnUiThread(() -> alertBuilder.show());
    }

    public static void toast(Activity activity, String message) {
        Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        activity.runOnUiThread(() -> toast.show());
    }
}
