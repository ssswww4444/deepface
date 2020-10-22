package au.edu.unimelb.eresearch.happypeople.ui;

import androidx.recyclerview.widget.RecyclerView;
import au.edu.unimelb.eresearch.happypeople.Constants;
import au.edu.unimelb.eresearch.happypeople.R;
import au.edu.unimelb.eresearch.happypeople.adapters.PredictionRVAdapter;
import au.edu.unimelb.eresearch.happypeople.model.Prediction;
import au.edu.unimelb.eresearch.happypeople.utils.Alert;
import au.edu.unimelb.eresearch.happypeople.utils.Api;
import au.edu.unimelb.eresearch.happypeople.utils.GeneralUtils;
import au.edu.unimelb.eresearch.happypeople.utils.PlayStoreUtils;
import au.edu.unimelb.eresearch.happypeople.utils.SharedPref;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PredictResultActivity extends BaseActivity implements PredictionRVAdapter.PredictFeedbackListener {
    private static final String TAG = "PredictResultActivity";
    public static final String EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI";

    private ImageView imgPrediction;
    private ImageView imgAddFeedbackIcon;
    private TextView tvPrediction;
    private TextView tvAddFeedback;
    private TextView tvSendFeedback;
    private ProgressBar loadingBar;
    private LinearLayout containerBtnAddFeedback;
    private LinearLayout containerBtnSendFeedback;

    private PredictionRVAdapter predictionRVAdapter;

    private ArrayList<Prediction> predictions = new ArrayList<>();
    private Boolean showingFeedback = false;
    private Boolean feedbackSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict_result);

        String imageUri = getIntent().getStringExtra(EXTRA_IMAGE_URI);

        RecyclerView rvPrediction = findViewById(R.id.rv_prediction);
        imgPrediction = findViewById(R.id.img_prediction);
        imgAddFeedbackIcon = findViewById(R.id.img_add_feedback_icon);
        tvPrediction = findViewById(R.id.tv_result);
        tvAddFeedback = findViewById(R.id.tv_add_feedback);
        tvSendFeedback = findViewById(R.id.tv_send_feedback);
        loadingBar = findViewById(R.id.loading);
        containerBtnAddFeedback = findViewById(R.id.container_btn_add_feedback);
        containerBtnSendFeedback = findViewById(R.id.container_btn_send_feedback);

        containerBtnAddFeedback.setOnClickListener(view -> toggleFeedback());
        containerBtnSendFeedback.setOnClickListener(view -> sendFeedback());

        predictionRVAdapter = new PredictionRVAdapter(PredictResultActivity.this, predictions, showingFeedback, feedbackSent);
        rvPrediction.setAdapter(predictionRVAdapter);

        // Send image to predict
        try {
            if (imageUri != null) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                        Uri.parse(imageUri));
                Bitmap fixedBitmap = GeneralUtils.modifyOrientation(this, bitmap, Uri.parse(imageUri));
                Glide.with(this).asBitmap().load(fixedBitmap).into(imgPrediction);
                predictImage(GeneralUtils.compressAndEncodeToBase64(fixedBitmap));
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to use image: " + e.getMessage());
            tvPrediction.setVisibility(View.VISIBLE);
            tvPrediction.setText("Something went wrong with your chosen file, please try a different image.");
        }

        setupToolbar("", true);
    }

    @Override
    public void updatePredictionFeedback(int id, boolean ageCorrect, boolean genderCorrect, boolean emotionCorrect) {
        for (int i = 0; i < this.predictions.size(); i++) {
            Prediction prediction = this.predictions.get(i);
            if (prediction.getId() == id) {
                prediction.setAgeFeedbackCorrect(ageCorrect);
                prediction.setGenderFeedbackCorrect(genderCorrect);
                prediction.setEmotionFeedbackCorrect(emotionCorrect);
                this.predictions.set(i, prediction);
                break;
            }
        }
    }

    /**
     * Toggle between showing/hiding of feedback
     */
    private void toggleFeedback() {
        boolean showingFeedback = !this.showingFeedback;
        String addFeedbackBtnDesc = showingFeedback ? "Hide Feedback" : feedbackSent ? "Show Feedback" : "Add Feedback";

        this.showingFeedback = showingFeedback;
        this.containerBtnSendFeedback.setVisibility(showingFeedback ? View.VISIBLE : View.GONE);
        this.tvAddFeedback.setText(addFeedbackBtnDesc);
        this.imgAddFeedbackIcon.setImageResource(showingFeedback ? R.drawable.ic_remove : R.drawable.ic_add);

        predictionRVAdapter.showFeedback(showingFeedback);
    }

    private void sendFeedback() {
        containerBtnSendFeedback.setClickable(false);  // temporarily disable button to prevent multiple feedback being sent
        loadingBar.setVisibility(View.VISIBLE);

        JSONObject body = new JSONObject();
        try {
            JSONArray feedbackData = new JSONArray();
            for (Prediction prediction: predictions) {
                feedbackData.put(prediction.getFeedbackJSON());
            }
            body.put("data", feedbackData);

            Api.shared.postFeedback(body, new Api.ApiCallback() {
                @Override
                public void pass(JSONObject result) {
                    Log.d(TAG, "Sent feedback");
                    feedbackSent = true;

                    // Disable "Send feedback" button after it is sent (only allow one feedback per prediction)
                    runOnUiThread(() -> {
                        loadingBar.setVisibility(View.GONE);
                        containerBtnSendFeedback.setBackgroundColor(getColor(R.color.light_gray_btn_bg));
                        tvSendFeedback.setText("Feedback Sent");
                        predictionRVAdapter.updateFeedbackSent(feedbackSent);

                        Alert.alert(PredictResultActivity.this, Constants.ALERT_TITLE_THANK_YOU,
                                Constants.ALERT_MSG_FEEDBACK_SUCCESS, Constants.ALERT_BTN_OK);
                    });
                }

                @Override
                public void fail(Exception e, String description) {
                    Log.d(TAG, "Failed to send feedback, error: " + e.getMessage());
                    runOnUiThread(() -> {
                        loadingBar.setVisibility(View.GONE);
                        containerBtnSendFeedback.setClickable(true);  // Re-enable button to allow for retry
                    });
                    Alert.alert(PredictResultActivity.this, Constants.ALERT_TITLE_ERROR_OOPS,
                            Constants.ALERT_MSG_ERROR_DEFAULT, Constants.ALERT_BTN_RETURN);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void predictImage(String image) {
        runOnUiThread(() -> loadingBar.setVisibility(View.VISIBLE));

        JSONObject body = new JSONObject();
        try {
            body.put("image", image);
            Api.shared.getPrediction(body, new Api.ApiCallback() {
                @Override
                public void pass(JSONObject result) {
                    Log.d(TAG, "Received image prediction results");
                    runOnUiThread(() -> loadingBar.setVisibility(View.GONE));

                    // Set message (if any)
                    try {
                        JSONArray messages = result.getJSONArray("message");
                        String message = messages.getString(0);
                        runOnUiThread(() -> {
                            tvPrediction.setVisibility(View.VISIBLE);
                            tvPrediction.setText(message);
                        });
                    } catch (JSONException e) {
                        runOnUiThread(() -> tvPrediction.setVisibility(View.GONE));
                    }

                    // Set image and predicted result
                    JSONArray predictionResult = new JSONArray();
                    try {
                        predictionResult = result.getJSONArray("results");
                        String imageResult = result.getString("img_str");

                        for (int i = 0; i < predictionResult.length(); i++) {
                            predictions.add(new Prediction(predictionResult.getJSONObject(i)));
                        }

                        runOnUiThread(() -> {
                            imgPrediction.setImageBitmap(GeneralUtils.decodeToBitmap(imageResult));
                            predictionRVAdapter.notifyDataSetChanged();
                        });
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    if (predictionResult.length() > 0) {
                        // Only show feedback button when there are result(s)
                        runOnUiThread(() -> {
                            containerBtnAddFeedback.setVisibility(View.VISIBLE);
                        });

                        // Prompt user rating 2 seconds after a successful prediction
                        SharedPreferences sharedPref = SharedPref.getPref();
                        int predictionsCount = sharedPref.getInt(SharedPref.PREDICTIONS_COUNT, 0) + 1;
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(SharedPref.PREDICTIONS_COUNT, predictionsCount);
                        editor.commit();

                        if (PlayStoreUtils.shouldReview(predictionsCount)) {
                            runOnUiThread(() -> {
                                final Handler handler = new Handler();
                                handler.postDelayed(() -> PlayStoreUtils.requestReview(
                                        getApplicationContext(), PredictResultActivity.this), 2000);
                            });
                        }
                    }
                }

                @Override
                public void fail(Exception e, String description) {
                    Log.d(TAG, "Failed to predict image, error: " + e.getMessage());
                    runOnUiThread(() -> loadingBar.setVisibility(View.GONE));
                    Alert.alert(PredictResultActivity.this, Constants.ALERT_TITLE_ERROR_OOPS,
                            Constants.ALERT_MSG_ERROR_DEFAULT, Constants.ALERT_BTN_OK);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
