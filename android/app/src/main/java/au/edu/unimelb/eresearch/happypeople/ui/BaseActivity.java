package au.edu.unimelb.eresearch.happypeople.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import au.edu.unimelb.eresearch.happypeople.utils.SharedPref;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Re-initialize Shared Preference on creating new activity
        SharedPref.initialize(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected void setupToolbar(String title, Boolean backButton) {
        try {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(backButton);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
