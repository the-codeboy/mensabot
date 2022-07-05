package ml.codeboy.thebot.events;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class MessageCommandEvent extends CommandEvent {

    private Message reply = null;

    public MessageCommandEvent(MessageReceivedEvent jdaEvent) {
        super(jdaEvent);
    }

    @Override
    public void reply(String message) {
        MessageReceivedEvent event = getMessageReceivedEvent();
        reply = event.getChannel().sendMessage(message).complete();
    }

    @Override
    public void reply(MessageEmbed... embed) {
        reply = getMessageReceivedEvent().getChannel().sendMessageEmbeds(Arrays.asList(embed)).complete();
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
    public void edit(String message) {
        if (reply == null)
            throw new IllegalStateException("Can not edit message without replying first");
        reply = reply.editMessage(message).complete();
    }

    @Override
    public void edit(MessageEmbed... embed) {
        if (reply == null)
            throw new IllegalStateException("Can not edit message without replying first");
        reply = reply.editMessageEmbeds(embed).complete();
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
