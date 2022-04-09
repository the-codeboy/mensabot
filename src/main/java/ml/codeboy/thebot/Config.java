package ml.codeboy.thebot;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
    public String upvoteEmote="903336533992550420", downVoteEmote ="903336514644222033";

    public static Config getInstance() {
        return instance;
    }
}
