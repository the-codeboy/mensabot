package ml.codeboy.thebot.data;

import com.github.codeboy.OpenMensa;
import com.github.codeboy.api.Mensa;
import com.google.gson.Gson;
import ml.codeboy.thebot.Config;
import net.dv8tion.jda.api.entities.Guild;

import java.io.FileWriter;
import java.io.IOException;

public class GuildData {
    private final transient Guild guild;
    private String guildId;
    private int defaultMensaId=187;
    private String updateChannelId="";

    public GuildData(Guild guild) {
        this.guild = guild;
        guildId=guild.getId();
    }


    public Guild getGuild() {
        return guild;
    }


    public Mensa getDefaultMensa(){
        return OpenMensa.getInstance().getMensa(getDefaultMensaId());
    }

    public int getDefaultMensaId() {
        return defaultMensaId;
    }


    public String getUpdateChannelId() {
        return updateChannelId;
    }

    public void setUpdateChannelId(String updateChannelId) {
        this.updateChannelId = updateChannelId;
        save();
    }

    public void setDefaultMensaId(int defaultMensaId) {
        this.defaultMensaId = defaultMensaId;
        save();
    }

    public void save(){
        GuildManager.getInstance().save(this);
    }

    public String getId() {
        return guildId;
    }
}
