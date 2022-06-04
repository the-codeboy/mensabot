package ml.codeboy.thebot.data;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

public class UserData {
    private final String userId;
    private int bedTime = -1;
    private int karma = 0;
    private Map<String,Integer> ratings=new HashMap<>();

    public UserData(String userId) {
        this.userId = userId;
    }

    public int getBedTime() {
        return bedTime;
    }

    public void setBedTime(int bedTime) {
        this.bedTime = bedTime;
    }

    public String getId() {
        return userId;
    }

    public User getUser(JDA jda) {
        return jda.retrieveUserById(getId()).complete();
    }

    public String getTag(JDA jda){
        try {
            return getUser(jda).getAsTag();
        }catch (Exception ignored){
        }
        return "unknown user";
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public void addRating(String meal,int rating){
        if (ratings == null)
            ratings = new HashMap<>();
        if (ratings.containsKey(meal)) {
            FoodRatingManager.getInstance().removeRating(meal, ratings.get(meal));
        } else karma++;//add one karma for rating
        FoodRatingManager.getInstance().addRating(meal,rating);
        ratings.put(meal,rating);
        UserDataManager.getInstance().save(this);
    }

    public void clearRatings() {
        if(ratings!=null&&!ratings.isEmpty()) {
            ratings = new HashMap<>();
            UserDataManager.getInstance().save(this);
        }
    }
}
