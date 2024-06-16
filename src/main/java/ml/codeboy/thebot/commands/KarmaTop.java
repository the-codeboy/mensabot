package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.data.UserData;
import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.listeners.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;

public class KarmaTop extends Command {
    public static final long updateInterval = 6000;
    private CommandEvent latestEvent;
    private MessageEmbed karmaTop = new EmbedBuilder().setTitle("Loading KarmaTop")
            .setDescription("please wait a few seconds\nI will update the message when I'm done").setColor(Color.RED).build();
    private long lastUpdated = 0;

    public KarmaTop() {
        super("KarmaTop", "Karma Bestenliste", "kt");
        setGuildOnlyCommand(false);
    }

    @Override
    public void register(CommandHandler handler) {
        super.register(handler);
//        updateKarmaTop(handler.getServer().getJDA());//initialisation is done when the command is first run
    }

    @Override
    public void run(CommandEvent event) {
        latestEvent = event;
        event.reply(getKarmaTop(event.getJdaEvent().getJDA()));
    }

    private MessageEmbed getKarmaTop(JDA jda) {
        if (lastUpdated + updateInterval < System.currentTimeMillis()) {
            updateKarmaTop(jda);
        }
        return karmaTop;
    }

    private void updateKarmaTop(JDA jda) {
        new Thread(() -> {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("KarmaTop");
            List<UserData> karmaTop = UserDataManager.getInstance().getKarmaSorted();
            for (int i = 0; i < 10; i++) {
                UserData data = karmaTop.get(i);
                builder.addField(i + 1 + ".", data.getTag(jda) + " " + data.getKarma(), false);
            }
            this.karmaTop = builder.build();
            lastUpdated = System.currentTimeMillis();
            if (latestEvent != null)
                latestEvent.edit(this.karmaTop);
        }).start();
    }
}
