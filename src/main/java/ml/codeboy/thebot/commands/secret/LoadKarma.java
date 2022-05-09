package ml.codeboy.thebot.commands.secret;

import ml.codeboy.thebot.Config;
import ml.codeboy.thebot.data.UserData;
import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadKarma extends SecretCommand {
    public LoadKarma() {
        super("loadKarma", "", "lk");
    }

    @Override
    public void run(CommandEvent event) {
        int messagesAmount = 1000;
        try {
            messagesAmount = Integer.parseInt(event.getArgs()[1]);
        } catch (Exception ignored) {
        }
        HashMap<User, Integer> karmaMap = new HashMap<>();
        Guild guild = event.getJdaEvent().getJDA().getGuildById(event.getArgs()[0]);
        event.reply("loading karma for " + guild.getName());
        for (GuildChannel channel : guild.getChannels()) {
            if (channel instanceof TextChannel) {

                try {
                    event.reply("loading karma for " + channel.getAsMention());
                    TextChannel tc = (TextChannel) channel;
                    List<Message> messages = tc.getIterableHistory().takeAsync(messagesAmount)
                            .thenApply(ArrayList::new).get();
                    for (Message message : messages) {
                        User user = message.getAuthor();
                        int karma = karmaMap.getOrDefault(user, 0);
                        for (MessageReaction r : message.getReactions()) {
                            if (r.getReactionEmote().isEmote()) {
                                int multiplier = 0;
                                if (Config.getInstance().isDownvote(r.getReactionEmote().getEmote().getId()))
                                    multiplier = 1;
                                if (Config.getInstance().isUpvote(r.getReactionEmote().getEmote().getId()))
                                    multiplier = -1;
                                karma += r.getCount() * multiplier;
                            }
                        }
                        karmaMap.put(user, karma);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        event.reply("finished loading karma for " + karmaMap.keySet().size() + " users");
        for (User user : karmaMap.keySet()) {
            UserData data = UserDataManager.getInstance().getData(user);
            int karma = data.getKarma();
            data.setKarma(karmaMap.get(user));
            if (karma != data.getKarma())//only save if changed
                UserDataManager.getInstance().save(data);
        }
        event.reply("finished saving karma");
    }
}
