package ml.codeboy.thebot.events;

import ml.codeboy.thebot.Config;
import ml.codeboy.thebot.commands.Command;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.util.Arrays;

public class MessageCommandEvent extends CommandEvent {

    private Message reply = null;

    public MessageCommandEvent(MessageReceivedEvent jdaEvent, Command command) {
        super(jdaEvent, command);
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
    public void reply(File file, String name) {
        reply = getMessageReceivedEvent().getChannel().sendFile(file, name).complete();
    }

    @Override
    public void reply(String message, File file, String name) {
        reply = getMessageReceivedEvent().getChannel().sendMessage(message).addFile(file, name).complete();
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

    public String[]getArgs(){
        return getContent().split(" ");
    }

    public String getContent(){
        String content = getMessageReceivedEvent().getMessage().getContentRaw();
        content = content.replaceFirst(Config.getInstance().prefix, "");

        String[] splitContent = content.split(" ", 2);

        if (splitContent.length == 1)
            return "";

        return splitContent[1];
    }


}
