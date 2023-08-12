package ml.codeboy.thebot.data;

import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.Guild;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuildManager {
    private static final String guildFolder = "guilds";

    private static final GuildManager instance = new GuildManager();
    private final HashMap<String, GuildData> guildData = new HashMap<>();

    private GuildManager() {
    }

    public static GuildManager getInstance() {
        return instance;
    }

    public GuildData getData(Guild guild) {
        GuildData data = guildData.get(guild.getId());
        if (data != null) {
            return data;
        }
        try {
            return loadData(guild);
        } catch (FileNotFoundException ignored) {
        }
        data = new GuildData(guild);
        guildData.put(guild.getId(), data);
        return data;
    }

    public List<GuildData> getAllGuildData() {
        return new ArrayList<>(guildData.values());
    }


    public GuildData loadData(Guild guild) throws FileNotFoundException {
        return loadData(guild.getId());
    }


    private GuildData loadData(String id) throws FileNotFoundException {
        GuildData data = new Gson().fromJson(new FileReader(guildFolder + File.separator + id), GuildData.class);
        return data;
    }

    public void save(GuildData data) {
        try {
            new File(guildFolder).mkdirs();
            FileWriter writer = new FileWriter(guildFolder + File.separator + data.getId());
            new Gson().toJson(data, writer);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
