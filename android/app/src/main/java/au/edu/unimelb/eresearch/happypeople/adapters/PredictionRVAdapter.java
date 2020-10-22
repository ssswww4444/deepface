package au.edu.unimelb.eresearch.happypeople.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import au.edu.unimelb.eresearch.happypeople.R;
import au.edu.unimelb.eresearch.happypeople.model.Prediction;

public class PredictionRVAdapter extends RecyclerView.Adapter<PredictionRVAdapter.ViewHolder> {
    private Context context;
    private List<Prediction> mData;
    private boolean showFeedback;
    private boolean feedbackSent;
    private PredictFeedbackListener predictFeedbackListener;

    public PredictionRVAdapter(Context context, List<Prediction> data, boolean showFeedback, boolean feedbackSent) {
        this.context = context;
        this.mData = data;
        this.showFeedback = showFeedback;
        this.feedbackSent = feedbackSent;

        try {
            this.predictFeedbackListener = (PredictFeedbackListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement PredictFeedbackListener");
        }
    }

    @Override
    public PredictionRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row_prediction,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Prediction prediction = mData.get(position);
        holder.setData(prediction, mData.size() == 1, showFeedback, feedbackSent);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void showFeedback(boolean show) {
        this.showFeedback = show;
        notifyDataSetChanged();
    }

    public void updateFeedbackSent(boolean sent) {
        this.feedbackSent = sent;
        notifyDataSetChanged();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout container;
        LinearLayout containerAgeFeedback;
        LinearLayout containerGenderFeedback;
        LinearLayout containerEmotionFeedback;
        ImageButton btnAgeFeedbackCorrect;
        ImageButton btnAgeFeedbackWrong;
        ImageButton btnGenderFeedbackCorrect;
        ImageButton btnGenderFeedbackWrong;
        ImageButton btnEmotionFeedbackCorrect;
        ImageButton btnEmotionFeedbackWrong;
        TextView tvId;
        TextView tvAge;
        TextView tvGender;
        TextView tvBestEmotion;
        TextView tvAllEmotions;

        Prediction prediction;
        ColorStateList colorGreen = ColorStateList.valueOf(context.getColor(R.color.green));
        ColorStateList colorGray = ColorStateList.valueOf(context.getColor(R.color.light_gray_btn_bg));
        ColorStateList colorRed = ColorStateList.valueOf(context.getColor(R.color.red));

        ViewHolder(View itemView) {
            super(itemView);
            // containers
            container = itemView.findViewById(R.id.container);
            containerAgeFeedback = itemView.findViewById(R.id.container_age_feedback);
            containerGenderFeedback = itemView.findViewById(R.id.container_gender_feedback);
            containerEmotionFeedback = itemView.findViewById(R.id.container_emotion_feedback);

            // buttons
            btnAgeFeedbackCorrect = itemView.findViewById(R.id.btn_age_feedback_correct);
            btnAgeFeedbackWrong = itemView.findViewById(R.id.btn_age_feedback_wrong);
            btnGenderFeedbackCorrect = itemView.findViewById(R.id.btn_gender_feedback_correct);
            btnGenderFeedbackWrong = itemView.findViewById(R.id.btn_gender_feedback_wrong);
            btnEmotionFeedbackCorrect = itemView.findViewById(R.id.btn_emotion_feedback_correct);
            btnEmotionFeedbackWrong = itemView.findViewById(R.id.btn_emotion_feedback_wrong);

            // set text views
            tvId = itemView.findViewById(R.id.tv_id);
            tvAge = itemView.findViewById(R.id.tv_age);
            tvGender = itemView.findViewById(R.id.tv_gender);
            tvBestEmotion = itemView.findViewById(R.id.tv_best_emotion);
            tvAllEmotions = itemView.findViewById(R.id.tv_all_emotions);

            // set feedback
            btnAgeFeedbackCorrect.setOnClickListener(view -> setAgeFeedback(true));
            btnAgeFeedbackWrong.setOnClickListener(view -> setAgeFeedback(false));
            btnGenderFeedbackCorrect.setOnClickListener(view -> setGenderFeedback(true));
            btnGenderFeedbackWrong.setOnClickListener(view -> setGenderFeedback(false));
            btnEmotionFeedbackCorrect.setOnClickListener(view -> setEmotionFeedback(true));
            btnEmotionFeedbackWrong.setOnClickListener(view -> setEmotionFeedback(false));
        }

        protected void setData(Prediction prediction, boolean hideId, boolean showFeedback, boolean feedbackSent) {
            this.prediction = prediction;
            String allEmotions = "(" + TextUtils.join(", ", prediction.getAllEmotions()) + ")";

            // set data
            this.tvId.setText(Integer.toString(prediction.getId()));
            this.tvAge.setText(prediction.getAge());
            this.tvGender.setText(prediction.getGender());
            this.tvBestEmotion.setText(prediction.getMostLikelyEmotion());
            this.tvAllEmotions.setText(allEmotions);

            // Hide ID if there is only 1 prediction result
            if (hideId) {
                this.container.setVisibility(View.GONE);
            }

            // Disable feedback buttons once feedback is sent
            if (feedbackSent) {
                this.btnAgeFeedbackCorrect.setEnabled(false);
                this.btnAgeFeedbackWrong.setEnabled(false);
                this.btnGenderFeedbackCorrect.setEnabled(false);
                this.btnGenderFeedbackWrong.setEnabled(false);
                this.btnEmotionFeedbackCorrect.setEnabled(false);
                this.btnEmotionFeedbackWrong.setEnabled(false);
            }
            toggleFeedback(showFeedback);

        }

        private void setAgeFeedback(boolean correct) {
            if (correct) {
                btnAgeFeedbackCorrect.setBackgroundTintList(colorGreen);
                btnAgeFeedbackCorrect.setColorFilter(ContextCompat.getColor(context, R.color.white));
                btnAgeFeedbackWrong.setBackgroundTintList(colorGray);
                btnAgeFeedbackWrong.setColorFilter(ContextCompat.getColor(context, R.color.light_gray_btn));
            } else {
                btnAgeFeedbackCorrect.setBackgroundTintList(colorGray);
                btnAgeFeedbackCorrect.setColorFilter(ContextCompat.getColor(context, R.color.light_gray_btn));
                btnAgeFeedbackWrong.setBackgroundTintList(colorRed);
                btnAgeFeedbackWrong.setColorFilter(ContextCompat.getColor(context, R.color.white));
            }
            this.prediction.setAgeFeedbackCorrect(correct);
            predictFeedbackListener.updatePredictionFeedback(this.prediction.getId(), correct,
                    prediction.getGenderFeedbackCorrect(), prediction.getEmotionFeedbackCorrect());
        }

        private void setGenderFeedback(boolean correct) {
            if (correct) {
                btnGenderFeedbackCorrect.setBackgroundTintList(colorGreen);
                btnGenderFeedbackCorrect.setColorFilter(ContextCompat.getColor(context, R.color.white));
                btnGenderFeedbackWrong.setBackgroundTintList(colorGray);
                btnGenderFeedbackWrong.setColorFilter(ContextCompat.getColor(context, R.color.light_gray_btn));
            } else {
                btnGenderFeedbackCorrect.setBackgroundTintList(colorGray);
                btnGenderFeedbackCorrect.setColorFilter(ContextCompat.getColor(context, R.color.light_gray_btn));
                btnGenderFeedbackWrong.setBackgroundTintList(colorRed);
                btnGenderFeedbackWrong.setColorFilter(ContextCompat.getColor(context, R.color.white));
            }
            this.prediction.setGenderFeedbackCorrect(correct);
            predictFeedbackListener.updatePredictionFeedback(this.prediction.getId(), prediction.getAgeFeedbackCorrect(),
                    correct, prediction.getEmotionFeedbackCorrect());
        }

        private void setEmotionFeedback(boolean correct) {
            if (correct) {
                btnEmotionFeedbackCorrect.setBackgroundTintList(colorGreen);
                btnEmotionFeedbackCorrect.setColorFilter(ContextCompat.getColor(context, R.color.white));
                btnEmotionFeedbackWrong.setBackgroundTintList(colorGray);
                btnEmotionFeedbackWrong.setColorFilter(ContextCompat.getColor(context, R.color.light_gray_btn));
            } else {
                btnEmotionFeedbackCorrect.setBackgroundTintList(colorGray);
                btnEmotionFeedbackCorrect.setColorFilter(ContextCompat.getColor(context, R.color.light_gray_btn));
                btnEmotionFeedbackWrong.setBackgroundTintList(colorRed);
                btnEmotionFeedbackWrong.setColorFilter(context.getColor(R.color.white));
            }
            this.prediction.setEmotionFeedbackCorrect(correct);
            predictFeedbackListener.updatePredictionFeedback(this.prediction.getId(), prediction.getAgeFeedbackCorrect(),
                    prediction.getGenderFeedbackCorrect(), correct);
        }

        private void toggleFeedback(boolean show) {
            this.containerAgeFeedback.setVisibility(show ? View.VISIBLE : View.GONE);
            this.containerGenderFeedback.setVisibility(show ? View.VISIBLE : View.GONE);
            this.containerEmotionFeedback.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public interface PredictFeedbackListener {
        void updatePredictionFeedback(int id, boolean ageCorrect, boolean genderCorrect, boolean emotionCorrect);
    }
}
