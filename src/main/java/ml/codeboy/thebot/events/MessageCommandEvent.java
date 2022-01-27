package ml.codeboy.thebot.events;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageCommandEvent extends CommandEvent {
    public MessageCommandEvent(MessageReceivedEvent jdaEvent) {
        super(jdaEvent);
    }

    @Override
    public void reply(String message) {
        MessageReceivedEvent event = getMessageReceivedEvent();
        event.getChannel().sendMessage(message).queue();
    }

    @Override
    public void reply(MessageEmbed embed) {
        getMessageReceivedEvent().getChannel().sendMessageEmbeds(embed).queue();
    }

    @Override
    public User getUser() {
        return getMessageReceivedEvent().getAuthor();
    }

    @Override
    public Member getMember() {
        return getMessageReceivedEvent().getMember();
    }

    @Override
    public Guild getGuild() {
        return getMessageReceivedEvent().getGuild();
    }

    @Override
    public MessageChannel getChannel() {
        return getMessageReceivedEvent().getChannel();
    }


}
