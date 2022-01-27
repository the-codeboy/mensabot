package ml.codeboy.thebot.events;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SlashCommandCommandEvent extends CommandEvent {
    public SlashCommandCommandEvent(SlashCommandEvent jdaEvent) {
        super(jdaEvent);
        jdaEvent.deferReply(isEphermal()).queue();
    }

    @Override
    public void reply(String message) {
        SlashCommandEvent event = getSlashCommandEvent();
        event.reply(message).queue();
    }

    @Override
    public void reply(MessageEmbed embed) {
        if(getSlashCommandEvent().isAcknowledged()) {
            getSlashCommandEvent().getHook().editOriginalEmbeds(embed).queue();
        }else
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

    @Override
    public MessageChannel getChannel() {
        return getSlashCommandEvent().getChannel();
    }
}
