package ml.codeboy.thebot.events;

import ml.codeboy.thebot.commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlashCommandCommandEvent extends CommandEvent {
    public SlashCommandCommandEvent(SlashCommandInteractionEvent jdaEvent, Command command) {
        super(jdaEvent, command);
        jdaEvent.deferReply(isEphermal()).queue();
    }

    @Override
    public void reply(String message) {
        SlashCommandInteractionEvent event = getSlashCommandEvent();
        event.getHook().sendMessage(message).queue();
    }

    @Override
    public void reply(MessageEmbed... embed) {
        List<Permission> suggestedPermissions = getCommand().getSuggestedPermissions(this);
        if (suggestedPermissions == null || suggestedPermissions.isEmpty())
            replyInternal(embed);
        else {
            List<MessageEmbed> embeds = new ArrayList<>(Arrays.asList(embed));
            EmbedBuilder builder = getBuilder();
            builder.setColor(Color.RED).setTitle("This command might not work properly");
            StringBuilder content = new StringBuilder("I am missing the following permissions: ");
            for (int i = 0; i < suggestedPermissions.size(); i++) {
                Permission permission = suggestedPermissions.get(i);
                content.append(permission.getName());
                if (i != suggestedPermissions.size() - 1)
                    content.append(", ");
            }
            builder.setDescription(content.toString());
            embeds.add(builder.build());
            replyInternal(embeds.toArray(embed));
        }
    }

    private void replyInternal(MessageEmbed[] embeds) {
        if (getSlashCommandEvent().isAcknowledged()) {
            getSlashCommandEvent().getHook().editOriginalEmbeds(embeds).queue();
        } else
            getSlashCommandEvent().replyEmbeds(Arrays.asList(embeds)).queue();
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
