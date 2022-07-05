package ml.codeboy.thebot;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Config {
    private static Config instance;

    static {
        try {
            instance = new Gson().fromJson(new FileReader("config.json"), Config.class);
        } catch (FileNotFoundException ignored) {
        }
        try {
            FileWriter writer = new FileWriter("config.json");
            new Gson().toJson(instance==null?(instance = new Config()):instance, writer);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String token = "token";
    public String serverId = "0";
    public String prefix = "!";
    public boolean quoteStatus = true;
    public HashSet<String> debugAccounts = new HashSet<String>(Arrays.asList("412330776886247424", "902979780394221648", "358247499531681803"));
    public List<String> upvoteEmotes = Arrays.asList("903336533992550420"), downVoteEmotes = Arrays.asList("903336514644222033");
    public String mongoDB_URL = "";
    public List<String> debugServers = Arrays.asList("0-966789128375140412");//ServerID-ChannelID //@Leo das ist, glaub ich, dein server ne?

    public String openWeatherApiKey = "";

    public static Config getInstance() {
        return instance;
    }


    public boolean isUpvote(String s) {
        return upvoteEmotes.contains(s);
    }

    public boolean isDownvote(String s){
        return downVoteEmotes.contains(s);
    }

    public boolean isDebugAccount(String s){ return debugAccounts.contains(s);}
}
