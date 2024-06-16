package com.the_codeboy.mensabot.commands.secret;

import com.the_codeboy.mensabot.Config;
import com.the_codeboy.mensabot.data.UserData;
import com.the_codeboy.mensabot.data.UserDataManager;
import com.the_codeboy.mensabot.events.CommandEvent;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadSusCount extends SecretCommand {
    public LoadSusCount() {
        super("loadSus", "", "ls");
    }

    @Override
    public void run(CommandEvent event) {
        new Thread(() -> load(event)).start();
    }

    private void load(CommandEvent event) {
        int messagesAmount = 1000;
        try {
            messagesAmount = Integer.parseInt(event.getArgs()[0]);
        } catch (Exception ignored) {
        }
        HashMap<User, Integer> suscountMap = new HashMap<>();
        int channels = 0;

        for (Guild guild : event.getJdaEvent().getJDA().getGuilds()) {
            event.reply("loading suscount for " + guild.getName());
            for (GuildChannel channel : guild.getChannels()) {
                channels++;
                if (channel instanceof TextChannel) {

                    try {
                        event.reply("loading suscount for " + channel.getAsMention());
                        TextChannel tc = (TextChannel) channel;
                        List<Message> messages = tc.getIterableHistory().takeAsync(messagesAmount)
                                .thenApply(ArrayList::new).get();
                        for (Message message : messages) {
                            User user = message.getAuthor();
                            int susCount = suscountMap.getOrDefault(user, 0);
                            for (MessageReaction r : message.getReactions()) {
                                if (r.getEmoji().getType() == Emoji.Type.CUSTOM) {
                                    if (Config.getInstance().isSus(r.getEmoji().getAsReactionCode()))
                                        susCount += r.getCount();
                                }
                            }
                            suscountMap.put(user, susCount);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        event.reply("finished loading suscount for " + suscountMap.keySet().size() + " users");
        event.reply("searched in " + channels + " in ca " + (channels * messagesAmount) + " messages");
        for (User user : suscountMap.keySet()) {
            UserData data = UserDataManager.getInstance().getData(user);
            int suscount = data.getSusCount();
            data.setSusCount(suscountMap.get(user));
            if (suscount != data.getSusCount())//only save if changed
                UserDataManager.getInstance().save(data);
        }
        event.reply("finished saving suscount");
    }
}
