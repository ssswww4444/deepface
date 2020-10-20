package au.edu.unimelb.eresearch.happypets.adapters;

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
import au.edu.unimelb.eresearch.happypets.R;
import au.edu.unimelb.eresearch.happypets.model.Prediction;

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
        LinearLayout containerBreedFeedback;
        LinearLayout containerEmotionFeedback;
        ImageButton btnBreedFeedbackCorrect;
        ImageButton btnBreedFeedbackWrong;
        ImageButton btnEmotionFeedbackCorrect;
        ImageButton btnEmotionFeedbackWrong;
        TextView tvId;
        TextView tvType;
        TextView tvBreed;
        TextView tvBestEmotion;
        TextView tvAllEmotions;

        Prediction prediction;
        ColorStateList colorGreen = ColorStateList.valueOf(context.getColor(R.color.green));
        ColorStateList colorGray = ColorStateList.valueOf(context.getColor(R.color.light_gray_btn_bg));
        ColorStateList colorRed = ColorStateList.valueOf(context.getColor(R.color.red));

        ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            containerBreedFeedback = itemView.findViewById(R.id.container_breed_feedback);
            containerEmotionFeedback = itemView.findViewById(R.id.container_emotion_feedback);
            btnBreedFeedbackCorrect = itemView.findViewById(R.id.btn_breed_feedback_correct);
            btnBreedFeedbackWrong = itemView.findViewById(R.id.btn_breed_feedback_wrong);
            btnEmotionFeedbackCorrect = itemView.findViewById(R.id.btn_emotion_feedback_correct);
            btnEmotionFeedbackWrong = itemView.findViewById(R.id.btn_emotion_feedback_wrong);
            tvId = itemView.findViewById(R.id.tv_id);
            tvType = itemView.findViewById(R.id.tv_type);
            tvBreed = itemView.findViewById(R.id.tv_breed);
            tvBestEmotion = itemView.findViewById(R.id.tv_best_emotion);
            tvAllEmotions = itemView.findViewById(R.id.tv_all_emotions);

            btnBreedFeedbackCorrect.setOnClickListener(view -> setBreedFeedback(true));
            btnBreedFeedbackWrong.setOnClickListener(view -> setBreedFeedback(false));
            btnEmotionFeedbackCorrect.setOnClickListener(view -> setEmotionFeedback(true));
            btnEmotionFeedbackWrong.setOnClickListener(view -> setEmotionFeedback(false));
        }

        protected void setData(Prediction prediction, boolean hideId, boolean showFeedback, boolean feedbackSent) {
            this.prediction = prediction;
            String allEmotions = "(" + TextUtils.join(", ", prediction.getAllEmotions()) + ")";

            this.tvId.setText(Integer.toString(prediction.getId()));
            this.tvType.setText(prediction.getPetType());
            this.tvBreed.setText(prediction.getBreed());
            this.tvBestEmotion.setText(prediction.getMostLikelyEmotion());
            this.tvAllEmotions.setText(allEmotions);

            // Hide ID if there is only 1 prediction result
            if (hideId) {
                this.container.setVisibility(View.GONE);
            }

            // Disable feedback buttons once feedback is sent
            if (feedbackSent) {
                this.btnBreedFeedbackCorrect.setEnabled(false);
                this.btnBreedFeedbackWrong.setEnabled(false);
                this.btnEmotionFeedbackCorrect.setEnabled(false);
                this.btnEmotionFeedbackWrong.setEnabled(false);
            }
            toggleFeedback(showFeedback);

        }

        private void setBreedFeedback(boolean correct) {
            if (correct) {
                btnBreedFeedbackCorrect.setBackgroundTintList(colorGreen);
                btnBreedFeedbackCorrect.setColorFilter(ContextCompat.getColor(context, R.color.white));
                btnBreedFeedbackWrong.setBackgroundTintList(colorGray);
                btnBreedFeedbackWrong.setColorFilter(ContextCompat.getColor(context, R.color.light_gray_btn));
            } else {
                btnBreedFeedbackCorrect.setBackgroundTintList(colorGray);
                btnBreedFeedbackCorrect.setColorFilter(ContextCompat.getColor(context, R.color.light_gray_btn));
                btnBreedFeedbackWrong.setBackgroundTintList(colorRed);
                btnBreedFeedbackWrong.setColorFilter(ContextCompat.getColor(context, R.color.white));
            }
            this.prediction.setBreedFeedbackCorrect(correct);
            predictFeedbackListener.updatePredictionFeedback(this.prediction.getId(), correct,
                    prediction.getEmotionFeedbackCorrect());
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
            predictFeedbackListener.updatePredictionFeedback(this.prediction.getId(),
                    prediction.getBreedFeedbackCorrect(), correct);
        }

        private void toggleFeedback(boolean show) {
            this.containerBreedFeedback.setVisibility(show ? View.VISIBLE : View.GONE);
            this.containerEmotionFeedback.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public interface PredictFeedbackListener {
        void updatePredictionFeedback(int id, boolean breedCorrect, boolean emotionCorrect);
    }
}
