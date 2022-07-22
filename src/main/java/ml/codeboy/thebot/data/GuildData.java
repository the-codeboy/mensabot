package ml.codeboy.thebot.data;

import com.github.codeboy.OpenMensa;
import com.github.codeboy.api.Mensa;
import ml.codeboy.thebot.apis.MensaVeganika;
import net.dv8tion.jda.api.entities.Guild;

public class GuildData {
    private final transient Guild guild;
    private final String guildId;
    private String latestAnnouncementId = "";
    private int defaultMensaId = 187;
    private String updateChannelId = "";

    public GuildData(Guild guild) {
        this.guild = guild;
        guildId = guild.getId();
    }


    public Guild getGuild() {
        return guild;
    }


    public Mensa getDefaultMensa() {
        Mensa mensa = OpenMensa.getInstance().getMensa(getDefaultMensaId());
        if (getId().equals("896116435875668019"))
            mensa = new MensaVeganika(mensa);//such a useless feature
        return mensa;
    }

    public int getDefaultMensaId() {
        return defaultMensaId;
    }

    public void setDefaultMensaId(int defaultMensaId) {
        this.defaultMensaId = defaultMensaId;
        save();
    }

    public String getUpdateChannelId() {
        return updateChannelId;
    }

    public void setUpdateChannelId(String updateChannelId) {
        this.updateChannelId = updateChannelId;
        save();
    }

    public void save() {
        GuildManager.getInstance().save(this);
    }

    public String getId() {
        return guildId;
    }

    //region getter setter
    public String getLatestAnnouncementId() {
        return latestAnnouncementId;
    }

    public void setLatestAnnouncementId(String latestAnnouncementId) {
        this.latestAnnouncementId = latestAnnouncementId;
    }
    //endregion
}
