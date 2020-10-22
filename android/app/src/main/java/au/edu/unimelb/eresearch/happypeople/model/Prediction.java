package au.edu.unimelb.eresearch.happypeople.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.lang.Math;

public class Prediction {
    private static final String TAG = "Prediction";

    private int id = 0;
    private int age = 0;
    private String gender = "";
    private JSONObject emotions = new JSONObject();

    private Boolean ageFeedbackCorrect = true;
    private Boolean genderFeedbackCorrect = true;
    private Boolean emotionFeedbackCorrect = true;

//    public Prediction(int id, String breed, String breedTitle, String petType, JSONObject emotions) {
//        this.id = id;
//        this.breed = breed;
//        this.breedTitle = breedTitle;
//        this.petType = petType;
//        this.emotions = emotions;
//    }

    public Prediction(JSONObject prediction) {
        try {
            this.id = prediction.getInt("id");
            this.age = prediction.getInt("age");
            this.gender = prediction.getString("gender");
            this.emotions = prediction.getJSONObject("emotion");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public JSONObject getEmotions() {
        return emotions;
    }

    public ArrayList<String> getAllEmotions() {
        ArrayList<String> allEmotions = new ArrayList<>();
        try {
            JSONArray jsonArray = this.emotions.getJSONArray("all");
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
            return this.emotions.getString("dominant");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

//    public String getMostLikelyEmotionTitle() {
//        try {
//            return this.emotions.getString("dominant");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    public Boolean getAgeFeedbackCorrect() {
        return ageFeedbackCorrect;
    }

    public Boolean getGenderFeedbackCorrect() {
        return genderFeedbackCorrect;
    }

    public void setAgeFeedbackCorrect(Boolean ageFeedbackCorrect) {
        this.ageFeedbackCorrect = ageFeedbackCorrect;
    }

    public void setGenderFeedbackCorrect(Boolean genderFeedbackCorrect) {
        this.genderFeedbackCorrect = genderFeedbackCorrect;
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
//            json.put("type", petType);
//            json.put("emotionFeedback", getMostLikelyEmotionTitle());
//            json.put("emotionCorrectness", emotionFeedbackCorrect);
//            json.put("breedFeedback", breedTitle);
//            json.put("breedCorrectness", breedFeedbackCorrect);

            json.put("ageFeedback", age);
            json.put("ageCorrectness", ageFeedbackCorrect);
            json.put("genderFeedback", gender);
            json.put("genderCorrectness", genderFeedbackCorrect);
            json.put("emotionFeedback", getMostLikelyEmotion());
            json.put("emotionCorrectness", emotionFeedbackCorrect);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(json);

        return json;
    }
}
