package ml.codeboy.thebot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.entities.User;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Config {
    private static Config instance;

    static {
        try {
            instance = new Gson().fromJson(new FileReader("config.json"), Config.class);
        } catch (FileNotFoundException ignored) {
        }
        try {
            FileWriter writer = new FileWriter("config.json");
            new Gson().toJson(instance == null ? (instance = new Config()) : instance, writer);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String token = "token";
    public String serverId = "0";
    public String prefix = "!";
    public boolean quoteStatus = true;
    public Set<String> debugAccounts = new HashSet<>(Arrays.asList("412330776886247424",
            "902979780394221648", "358247499531681803"));//eg contributers - for debugging
    public List<String> upvoteEmotes = Collections.singletonList("hochwaehli:903336533992550420"),
            downVoteEmotes = Collections.singletonList("runterwaehli:903336514644222033"),
            susEmotes = Collections.singletonList("sus:930765635913408532");
    public String mongoDB_URL = "";
    public List<String> debugChannels = Arrays.asList("993961018919235644", "966789128375140412");//will receive logs

    public Set<String> admins = new HashSet<>(Collections.singletonList("412330776886247424"));//can use secret commands

    public String dmDebugChannel = "966789128375140412";
    public String openWeatherApiKey = "";

    public static Config getInstance() {
        return instance;
    }

    public void save() throws IOException {
        FileWriter writer = new FileWriter("config.json");
        new GsonBuilder().setPrettyPrinting().create().toJson(instance);
        writer.close();
    }

    public boolean isUpvote(String s) {
        return upvoteEmotes.contains(s);
    }

    public boolean isDownvote(String s) {
        return downVoteEmotes.contains(s);
    }

    public boolean isSus(String s) {
        return susEmotes.contains(s);
    }

    public boolean isDebugAccount(User user) {
        return debugAccounts.contains(user.getId());
    }
}
