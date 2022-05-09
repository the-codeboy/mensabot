package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.CommandHandler;
import ml.codeboy.thebot.data.UserData;
import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;

public class KarmaBottom extends Command {
    public KarmaBottom() {
        super("KarmaBottom", "Karma Schlechtestenliste", "kb");
    }

    @Override
    public void register(CommandHandler handler) {
        updateKarmaTop(handler.getServer().getJDA());
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(getKarmaTop(event.getJdaEvent().getJDA()));
    }

    private MessageEmbed karmaTop = new EmbedBuilder().setTitle("Loading KarmaBottom")
            .setDescription("please use the command again").setColor(Color.RED).build();
    private long lastUpdated = 0;
    private final long updateInterval = 60000;

    private MessageEmbed getKarmaTop(JDA jda) {
        if (lastUpdated + updateInterval < System.currentTimeMillis()) {
            updateKarmaTop(jda);
        }
        return karmaTop;
    }

    private void updateKarmaTop(JDA jda) {
        new Thread(() -> {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("KarmaBottom")
                    .setDescription("Updated every 10 Minutes");
            List<UserData> karmaTop = UserDataManager.getInstance().getKarmaSorted();
            for (int i = 0; i < 10; i++) {
                UserData data = karmaTop.get(karmaTop.size()-(i+1));
                builder.addField(i + 1 + ".", data.getTag(jda) + " " + data.getKarma(), false);
            }
            this.karmaTop = builder.build();
            lastUpdated = System.currentTimeMillis();
        }).start();
    }
}
