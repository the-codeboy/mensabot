package com.the_codeboy.mensabot.listeners;

import com.the_codeboy.mensabot.Config;
import com.the_codeboy.mensabot.util.Util;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class KarmaListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        String emote = event.getReaction().getEmoji().getAsReactionCode();

        boolean upvote = Config.getInstance().isUpvote(emote);
        boolean downVote = Config.getInstance().isDownvote(emote);
        boolean sus = Config.getInstance().isSus(emote);

        if (upvote || downVote) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            Util.addKarma(message.getAuthor(), upvote ? 1 : -1);
        }

        if (sus) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            Util.addSusCount(message.getAuthor(), 1);
        }

    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        String emote = event.getReaction().getEmoji().getAsReactionCode();

        boolean upvote = Config.getInstance().isUpvote(emote);
        boolean downVote = Config.getInstance().isDownvote(emote);
        boolean sus = Config.getInstance().isSus(emote);

        if (upvote || downVote) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            Util.addKarma(message.getAuthor(), upvote ? -1 : 1);// removing upvotes => remove karma
        }

        if (sus) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            Util.addSusCount(message.getAuthor(), -1);
        }
    }
}
