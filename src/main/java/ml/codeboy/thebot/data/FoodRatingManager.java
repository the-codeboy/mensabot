package ml.codeboy.thebot.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class FoodRatingManager {

    private static final String filePath = "ratings" + File.separator + "ratings.json";
    private static final FoodRatingManager instance = new FoodRatingManager();

    private FoodRatingManager() {
        try {
            loadData();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static FoodRatingManager getInstance() {
        return instance;
    }

    private Map<String, Rating> ratings = new HashMap<>();

    public double getRating(String meal) {
        Rating rating = ratings.getOrDefault(meal, null);
        if (rating == null)
            return -1;
        return rating.getAverage();
    }

    public void addRating(String meal, int rating) {
        Rating r = ratings.computeIfAbsent(meal, m -> new Rating());
        r.addRating(rating);
        save();
    }

    public void removeRating(String meal, int rating) {
        Rating r = ratings.computeIfAbsent(meal, m -> new Rating());
        r.removeRating(rating);
        save();
    }

    private void loadData() throws FileNotFoundException {
        Type typeOfT = new TypeToken<Map<String, Rating>>() {
        }.getType();
        try {
            File file = new File(filePath);
            if (file.exists()) {
                ratings = new Gson().fromJson(new FileReader(file), typeOfT);
            }
        } catch (Exception ignored) {
        }
        if (ratings == null)
            ratings = new HashMap<>();
    }

    private void save() {
        try {
            new File(filePath).getParentFile().mkdirs();
            FileWriter writer = new FileWriter(filePath);
            new Gson().toJson(ratings, writer);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
