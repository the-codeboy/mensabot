package ml.codeboy.thebot.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class FoodRatingManager {

    private static final String filePath = "ratings" + File.separator + "ratings.json";
    private static final String imagesPath = "ratings" + File.separator + "images.json";
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
    private Map<String, Collection<MealImage>> images = new HashMap<>();

    public double getRating(String meal) {
        Rating rating = ratings.getOrDefault(meal, null);
        if (rating == null)
            return -1;
        return rating.getAverage();
    }

    public int getRatings(String meal) {
        Rating rating = ratings.getOrDefault(meal, null);
        if (rating == null)
            return -1;
        return rating.getRatings();
    }

    public void addRating(String meal, int rating) {
        Rating r = ratings.computeIfAbsent(meal, m -> new Rating());
        r.addRating(rating);
        saveRatings();
    }

    public void removeRating(String meal, int rating) {
        Rating r = ratings.computeIfAbsent(meal, m -> new Rating());
        r.removeRating(rating);
        saveRatings();
    }

    public MealImage addImage(String meal, String url, User author) {
        MealImage image = new MealImage(author.getId(), url);
        images.computeIfAbsent(meal, e -> new ArrayList<>()).add(image);
        saveImages();
        return image;
    }

    public String getImage(String meal) {
        for (MealImage image : images.getOrDefault(meal, Collections.emptyList())) {
            if (image != null && image.isAccepted() && image.getUrl() != null)
                return image.getUrl();
        }
        return "";
    }

    public MealImage getImage(UUID id) {
        for (Collection<MealImage> meals : images.values()) {
            for (MealImage meal : meals) {
                if (meal != null && meal.getId() != null && meal.getId().equals(id))
                    return meal;
            }
        }
        return null;
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
        typeOfT = new TypeToken<Map<String, Collection<MealImage>>>() {
        }.getType();
        try {
            File file = new File(imagesPath);
            if (file.exists()) {
                images = new Gson().fromJson(new FileReader(file), typeOfT);
            }
        } catch (Exception ignored) {
        }
        if (images == null)
            images = new HashMap<>();
    }

    private void saveRatings() {
        try {
            new File(filePath).getParentFile().mkdirs();
            FileWriter writer = new FileWriter(filePath);
            new Gson().toJson(ratings, writer);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveImages() {
        try {
            new File(imagesPath).getParentFile().mkdirs();
            FileWriter writer = new FileWriter(imagesPath);
            new Gson().toJson(images, writer);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
