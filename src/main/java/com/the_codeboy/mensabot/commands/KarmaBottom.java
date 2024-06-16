package com.the_codeboy.mensabot.commands;

import com.the_codeboy.mensabot.data.UserData;
import com.the_codeboy.mensabot.data.UserDataManager;
import com.the_codeboy.mensabot.events.CommandEvent;
import com.the_codeboy.mensabot.listeners.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;

public class KarmaBottom extends Command {
    private CommandEvent latestEvent;
    private MessageEmbed karmaTop = new EmbedBuilder().setTitle("Loading KarmaBottom")
            .setDescription("please wait a few seconds\nI will update the message when I'm done").setColor(Color.RED).build();
    private long lastUpdated = 0;

    public KarmaBottom() {
        super("KarmaBottom", "Karma Schlechtestenliste", "kb");
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
        if (lastUpdated + KarmaTop.updateInterval < System.currentTimeMillis()) {
            updateKarmaTop(jda);
        }
        return karmaTop;
    }

    private void updateKarmaTop(JDA jda) {
        new Thread(() -> {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("KarmaBottom");
            List<UserData> karmaTop = UserDataManager.getInstance().getKarmaSorted();
            for (int i = 0; i < 10; i++) {
                UserData data = karmaTop.get(karmaTop.size() - (i + 1));
                builder.addField(i + 1 + ".", data.getTag(jda) + " " + data.getKarma(), false);
            }
            this.karmaTop = builder.build();
            lastUpdated = System.currentTimeMillis();
            if (latestEvent != null)
                latestEvent.edit(this.karmaTop);
        }).start();
    }
}
