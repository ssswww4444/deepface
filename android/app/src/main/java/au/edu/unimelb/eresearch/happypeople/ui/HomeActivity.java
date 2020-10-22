package au.edu.unimelb.eresearch.happypeople.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import au.edu.unimelb.eresearch.happypeople.Constants;
import au.edu.unimelb.eresearch.happypeople.R;
import au.edu.unimelb.eresearch.happypeople.ui.fragments.AboutFragment;
import au.edu.unimelb.eresearch.happypeople.ui.fragments.HomeFragment;
import au.edu.unimelb.eresearch.happypeople.utils.GeneralUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Home screen
 */
public class HomeActivity extends BaseActivity {
    private static final String FRAG_HOME = "Fragment Home";
    private static final String FRAG_ABOUT = "Fragment About";

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigation;

    private String currentFragmentId;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);

        currentFragmentId = FRAG_HOME;
        fragmentManager = getSupportFragmentManager();

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navItemsListener);
        bottomNavigation.setSelectedItemId(R.id.tab_predict);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Constants.REQUEST_CAMERA:
                if (resultCode == RESULT_OK) {
                    predictImg(GeneralUtils.tempImgPath);
                }
                break;
            case Constants.REQUEST_GALLERY:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        predictImg(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void openFragment(String newFragId) {
        Fragment oldFragment = fragmentManager.findFragmentByTag(currentFragmentId);
        Fragment newFragment = fragmentManager.findFragmentByTag(newFragId);

        if (newFragment != null) {
            fragmentManager.beginTransaction().show(newFragment).commit();
        } else {
            switch (newFragId) {
                case FRAG_HOME:
                    newFragment = new HomeFragment();
                    break;
                case FRAG_ABOUT:
                    newFragment = new AboutFragment();
                    break;
                default:
                    newFragment = new HomeFragment();
                    break;
            }
            fragmentManager.beginTransaction().add(R.id.container, newFragment, newFragId).commit();
        }

        if (oldFragment != null){
            fragmentManager.beginTransaction().hide(oldFragment).commit();
        }

        currentFragmentId = newFragId;
    }

    private void predictImg(Uri uri) {
        Intent intent = new Intent(HomeActivity.this, PredictResultActivity.class);
        intent.putExtra(PredictResultActivity.EXTRA_IMAGE_URI, uri.toString());
        startActivity(intent);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navItemsListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.tab_predict:
                            toolbar.setVisibility(View.GONE);
                            openFragment(FRAG_HOME);
                            return true;
//                        case R.id.tab_breeds:
//                            toolbar.setVisibility(View.VISIBLE);
//                            toolbar.setTitle("Breeds");
//                            openFragment(FRAG_BREEDS);
//                            return true;
                        case R.id.tab_about:
                            toolbar.setVisibility(View.VISIBLE);
                            toolbar.setTitle("About");
                            openFragment(FRAG_ABOUT);
                            return true;
                    }
                    return false;
                }
            };
}
