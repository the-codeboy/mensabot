package ml.codeboy.thebot.util;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

import java.io.File;

public interface Replyable {
    static Replyable from(MessageChannel channel) {
        return (message, referenceMessage, files) -> {
            MessageAction messageAction = channel.sendMessage(message);
            for (File file : files) {
                messageAction.addFile(file);
            }
            messageAction.queue();
        };
    }

    static Replyable from(Message message) {
        return new Replyable() {
            @Override
            public void reply(Message content) {
                reply(content, false);
            }

            @Override
            public void reply(Message msg, boolean referenceMessage, File... files) {
                MessageAction messageAction;
                if (referenceMessage)
                    messageAction = message.reply(msg);
                else
                    messageAction = message.getChannel().sendMessage(msg);

                for (File file : files) {
                    messageAction.addFile(file);
                }
                messageAction.queue();
            }

            @Override
            public void reply(Message content, boolean referenceMessage) {
                if (referenceMessage)
                    message.reply(content).queue();
                else
                    message.getChannel().sendMessage(content).queue();
            }

        };
    }

    static Replyable from(IReplyCallback replyCallback) {
        return new Replyable() {
            @Override
            public void reply(Message message) {
                replyCallback.getHook().sendMessage(message).queue();
            }

            @Override
            public void reply(Message message, boolean referenceMessage, File... files) {
                WebhookMessageAction<Message> messageAction = replyCallback.getHook().sendMessage(message);
                for (File file : files) {
                    messageAction.addFile(file);
                }
                messageAction.queue();
            }
        };
    }

    default void reply(String message) {
        reply(new MessageBuilder(message).build());
    }

    default void reply(Message message, boolean referenceMessage) {
        reply(message, referenceMessage, new File[0]);
    }

    default void reply(MessageEmbed... embeds) {
        reply(new MessageBuilder().setEmbeds(embeds).build());
    }

    default void reply(Message message) {
        reply(message, false);
    }

    void reply(Message message, boolean referenceMessage, File... files);
}
