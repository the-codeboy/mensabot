package ml.codeboy.thebot.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RestaurantManager {
    private static final String filePath = "ratings" + File.separator + "restaurants.json";
    private static final RestaurantManager instance = new RestaurantManager();
    private List<Restaurant> restaurants = new ArrayList<>();


    private RestaurantManager() {
        try {
            loadData();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static RestaurantManager getInstance() {
        return instance;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public Restaurant getRestaurant(String name) {
        return restaurants.stream().filter(r -> r.getName().equals(name)).findFirst().orElse(null);
    }

    public boolean addRating(String name, int rating) {
        Restaurant restaurant = getRestaurant(name);
        if (restaurant == null)
            return false;
        restaurant.getRating().addRating(rating);
        return true;
    }

    public boolean removeRating(String name, int rating) {
        Restaurant restaurant = getRestaurant(name);
        if (restaurant == null)
            return false;
        restaurant.getRating().removeRating(rating);
        return true;
    }

    private void loadData() throws FileNotFoundException {
        Type typeOfT = new TypeToken<List<Restaurant>>() {
        }.getType();
        try {
            File file = new File(filePath);
            if (file.exists()) {
                restaurants = new Gson().fromJson(new FileReader(file), typeOfT);
            }
        } catch (Exception ignored) {
        }
        if (restaurants == null)
            restaurants = new ArrayList<>();
    }

    public void save() {
        try {
            new File(filePath).getParentFile().mkdirs();
            FileWriter writer = new FileWriter(filePath);
            new Gson().toJson(restaurants, writer);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
