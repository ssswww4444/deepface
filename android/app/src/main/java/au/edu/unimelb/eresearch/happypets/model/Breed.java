package au.edu.unimelb.eresearch.happypets.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Breed implements Serializable {
    private String id = "";
    private String breed = "";
    private String description = "";
    private String image = "";
    private String link = "";

    public Breed(String id, String breed, String description, String image, String link) {
        this.id = id;
        this.breed = breed;
        this.description = description;
        this.image = image;
        this.link = link;
    }

    public Breed(JSONObject prediction) {
        try {
            this.id = prediction.getString("index");
            this.breed = prediction.getString("breed");
            this.description = prediction.getString("description");
            this.image = prediction.getString("image");
            this.link = prediction.getString("link");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public String getBreed() {
        return breed;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getLink() {
        return link;
    }
}
