package au.edu.unimelb.eresearch.happypets.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Prediction {
    private static final String TAG = "Prediction";

    private int id = 0;
    private String breed = "";
    private String breedTitle = "";
    private String petType = "";
    private JSONObject emotions = new JSONObject();

    private Boolean breedFeedbackCorrect = true;
    private Boolean emotionFeedbackCorrect = true;

    public Prediction(int id, String breed, String breedTitle, String petType, JSONObject emotions) {
        this.id = id;
        this.breed = breed;
        this.breedTitle = breedTitle;
        this.petType = petType;
        this.emotions = emotions;
    }

    public Prediction(JSONObject prediction) {
        try {
            this.id = prediction.getInt("id");
            this.breed = prediction.getString("breed");
            this.breedTitle = prediction.getString("breedTitle");
            this.petType = prediction.getString("pet");
            this.emotions = prediction.getJSONObject("emotion");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public String getBreed() {
        return breed;
    }

    public String getBreedTitle() {
        return breedTitle;
    }

    public String getPetType() {
        return petType;
    }

    public JSONObject getEmotions() {
        return emotions;
    }

    public ArrayList<String> getAllEmotions() {
        ArrayList<String> allEmotions = new ArrayList<>();
        try {
            JSONArray jsonArray = this.emotions.getJSONArray("allEmotions");
            for (int i = 0; i < jsonArray.length(); i++) {
                allEmotions.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allEmotions;
    }

    public String getMostLikelyEmotion() {
        try {
            return this.emotions.getString("mostLikely");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getMostLikelyEmotionTitle() {
        try {
            return this.emotions.getString("mostLikelyTitle");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Boolean getBreedFeedbackCorrect() {
        return breedFeedbackCorrect;
    }

    public void setBreedFeedbackCorrect(Boolean breedFeedbackCorrect) {
        this.breedFeedbackCorrect = breedFeedbackCorrect;
    }

    public Boolean getEmotionFeedbackCorrect() {
        return emotionFeedbackCorrect;
    }

    public void setEmotionFeedbackCorrect(Boolean emotionFeedbackCorrect) {
        this.emotionFeedbackCorrect = emotionFeedbackCorrect;
    }

    public JSONObject getFeedbackJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("type", petType);
            json.put("emotionFeedback", getMostLikelyEmotionTitle());
            json.put("emotionCorrectness", emotionFeedbackCorrect);
            json.put("breedFeedback", breedTitle);
            json.put("breedCorrectness", breedFeedbackCorrect);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
