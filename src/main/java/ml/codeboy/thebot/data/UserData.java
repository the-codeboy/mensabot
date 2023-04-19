package ml.codeboy.thebot.data;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserData {
    @BsonId
    private final String userId;
    @BsonProperty
    private int bedTime = -1;
    @BsonProperty
    private int karma = 0;
    @BsonProperty
    private int susCount = 0;
    @BsonProperty
    private Map<String, Integer> ratings = new HashMap<>();
    @BsonProperty
    private Map<String, Integer> restaurantRatings = new HashMap<>();
    @BsonIgnore
    private ArrayList<Comment> comments = new ArrayList<>();
    @BsonCreator
    public UserData(@BsonId String userId) {
        this.userId = userId;
    }

    public int getBedTime() {
        return bedTime;
    }

    public void setBedTime(int bedTime) {
        this.bedTime = bedTime;
    }

    public String getUserId() {
        return userId;
    }

    public User getUser(JDA jda) {
        return jda.retrieveUserById(getUserId()).complete();
    }

    public String getTag(JDA jda) {
        try {
            return getUser(jda).getAsTag();
        } catch (Exception ignored) {
        }
        return "unknown user";
    }
    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public int getSusCount() {
        return susCount;
    }

    public void setSusCount(int susCount) {
        this.susCount = susCount;
    }

    public void addRating(String meal, int rating) {
        if (ratings == null)
            ratings = new HashMap<>();
        if (ratings.containsKey(meal)) {
            FoodRatingManager.getInstance().removeRating(meal, ratings.get(meal));
        } else karma++;//add one karma for rating
        FoodRatingManager.getInstance().addRating(meal, rating);
        ratings.put(meal, rating);
        UserDataManager.getInstance().save(this);
    }

    public boolean addRestaurantRating(String restaurant, int rating) {
        if (restaurantRatings == null)
            restaurantRatings = new HashMap<>();
        Restaurant r = RestaurantManager.getInstance().getRestaurant(restaurant);
        if (restaurant == null)
            return false;
        if (restaurantRatings.containsKey(restaurant)) {
            r.getRating().removeRating(restaurantRatings.get(restaurant));
        } else karma++;//add one karma for rating
        r.getRating().addRating(rating);
        restaurantRatings.put(restaurant, rating);
        RestaurantManager.getInstance().save();
        UserDataManager.getInstance().save(this);
        return true;
    }

    public void clearRatings() {
        if (ratings != null && !ratings.isEmpty()) {
            ratings = new HashMap<>();
            UserDataManager.getInstance().save(this);
        }
    }

    public ArrayList<Comment> getComments() {
        if (comments == null)
            comments = new ArrayList<>();
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public Map<String, Integer> getRatings() {
        return ratings;
    }

    public Map<String, Integer> getRestaurantRatings() {
        return restaurantRatings;
    }
}
