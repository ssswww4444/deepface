package au.edu.unimelb.eresearch.happypets.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.browser.customtabs.CustomTabsIntent;
import au.edu.unimelb.eresearch.happypets.R;
import au.edu.unimelb.eresearch.happypets.model.Breed;

/**
 * Activity for detailed breed information
 */
public class BreedDetailActivity extends BaseActivity {
    public static final String EXTRA_BREED = "EXTRA_BREED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breed_detail);

        if (getIntent() != null && getIntent().hasExtra(EXTRA_BREED)) {
            Breed breed = (Breed) getIntent().getSerializableExtra(EXTRA_BREED);
            if (breed != null) {
                initViews(breed);
            }
        }

        this.setupToolbar("", true);
    }

    private void initViews(Breed breed) {
        ImageView imgBreed = findViewById(R.id.img_breed);
        TextView tvBreed = findViewById(R.id.tv_breed_name);
        TextView tvDescription = findViewById(R.id.tv_breed_desc);
        Button btnWikiLink = findViewById(R.id.btn_wiki_link);

        Glide.with(BreedDetailActivity.this)
                .load(breed.getImage())
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(imgBreed);

        tvBreed.setText(breed.getBreed());
        tvDescription.setText(breed.getDescription());

        btnWikiLink.setOnClickListener((v) -> {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(breed.getLink()));
        });
    }
}
