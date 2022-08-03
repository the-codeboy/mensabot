package ml.codeboy.thebot.util;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

import java.util.Arrays;

public interface Replyable {
    static Replyable from(MessageChannel channel) {
        return new Replyable() {
            @Override
            public void reply(String message) {
                channel.sendMessage(message).queue();
            }

            @Override
            public void reply(MessageEmbed... embeds) {
                channel.sendMessageEmbeds(Arrays.asList(embeds)).queue();
            }
        };
    }

    static Replyable from(IReplyCallback replyCallback) {
        return new Replyable() {
            @Override
            public void reply(String message) {
                replyCallback.getHook().sendMessage(message).queue();
            }

            @Override
            public void reply(MessageEmbed... embeds) {
                replyCallback.getHook().sendMessageEmbeds(Arrays.asList(embeds)).queue();
            }
        };
    }

    void reply(String message);

    void reply(MessageEmbed... embeds);
}
