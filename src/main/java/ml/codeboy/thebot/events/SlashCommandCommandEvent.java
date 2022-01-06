package ml.codeboy.thebot.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SlashCommandCommandEvent extends CommandEvent {
    public SlashCommandCommandEvent(SlashCommandEvent jdaEvent) {
        super(jdaEvent);
    }

    @Override
    public void reply(String message) {
        SlashCommandEvent event = getSlashCommandEvent();
        event.reply(message).queue();
    }

    @Override
    public void reply(MessageEmbed embed) {
        getSlashCommandEvent().replyEmbeds(embed).queue();
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
}
