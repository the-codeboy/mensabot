package ml.codeboy.thebot.data;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class UserData {
    private String userId;
    private int bedTime = -1;
    private int karma = 0;

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
}
