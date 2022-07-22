package ml.codeboy.thebot.events;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.File;
import java.util.Arrays;

public class SlashCommandCommandEvent extends CommandEvent {
    public SlashCommandCommandEvent(SlashCommandInteractionEvent jdaEvent) {
        super(jdaEvent);
        jdaEvent.deferReply(isEphermal()).queue();
    }

    @Override
    public void reply(String message) {
        SlashCommandInteractionEvent event = getSlashCommandEvent();
        event.getHook().sendMessage(message).queue();
    }

    @Override
    public void reply(MessageEmbed... embed) {
        if (getSlashCommandEvent().isAcknowledged()) {
            getSlashCommandEvent().getHook().editOriginalEmbeds(embed).queue();
        } else
            getSlashCommandEvent().replyEmbeds(Arrays.asList(embed)).queue();
    }

    @Override
    public void reply(File file, String name) {
        if (getSlashCommandEvent().isAcknowledged()) {
            getSlashCommandEvent().getHook().editOriginal(file, name).complete();
        } else
            getSlashCommandEvent().replyFile(file, name).complete();
    }

    @Override
    public void reply(String message, File file, String name) {
        if (getSlashCommandEvent().isAcknowledged()) {
            getSlashCommandEvent().getHook().editOriginal(message).addFile(file, name).complete();
        } else
            getSlashCommandEvent().reply(message).addFile(file, name).complete();
    }

    @Override
    public Guild getGuild() {
        return getSlashCommandEvent().getGuild();
    }

    @Override
    public User getUser() {
        return getSlashCommandEvent().getUser();
    }

    @Override
    public Member getMember() {
        return getSlashCommandEvent().getMember();
    }

    @Override
    public void edit(String message) {
        getSlashCommandEvent().getHook().editOriginal(message).queue();
    }

    @Override
    public void edit(MessageEmbed... embed) {
        getSlashCommandEvent().getHook().editOriginalEmbeds(embed).queue();
    }

    @Override
    public MessageChannel getChannel() {
        return getSlashCommandEvent().getChannel();
    }
}
