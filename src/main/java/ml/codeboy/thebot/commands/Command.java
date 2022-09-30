package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.CommandHandler;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Command {
    private final String name, description;
    private final String[] aliases;
    private Permission[] requiredPermisions = new Permission[0];
    private Permission[] requiredBotPermisions = new Permission[0];
    private boolean hidden = false, guildOnlyCommand = true;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Command(String name, String description, String... aliases) {
        this.name = name;
        if (description == null || description.isEmpty())
            description = "unknown";
        this.description = description;
        this.aliases = aliases;
    }

    protected Permission[] getRequiredPermisions() {
        return requiredPermisions;
    }

    protected void setRequiredPermissions(Permission... requiredPermisions) {
        this.requiredPermisions = requiredPermisions;
    }

    public Permission[] getRequiredBotPermisions() {
        return requiredBotPermisions;
    }

    public Command setRequiredBotPermisions(Permission... requiredBotPermisions) {
        this.requiredBotPermisions = requiredBotPermisions;
        return this;
    }

    public void execute(CommandEvent event) {
        if (isGuildOnlyCommand() && event.getGuild() == null) {
            event.replyError("You can only use this command on a server. If you think this is a mistake just send the bot a message");
            return;
        }
        if (!hasPermission(event)) {
            event.reply("Insufficient permissions");
            return;
        }
        try {
            run(event);
        } catch (Exception e) {
            e.printStackTrace();
            event.replyError(e.getMessage());
        }
    }

    private boolean hasPermission(CommandEvent event) {
        Member member = event.getMember();
        if (member == null)
            return getRequiredPermisions().length == 0;
        return member.hasPermission(getRequiredPermisions());
    }

    public abstract void run(CommandEvent event);

    public SlashCommandData getCommandData() {
        return Commands.slash(getName().toLowerCase(), getDescription());
    }

    protected EmbedBuilder newBuilder() {//allows to configure default values for embeds like color
        EmbedBuilder newBuilder = new EmbedBuilder();
        newBuilder.setColor(Color.black);//Black—like my soul
        return newBuilder;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isGuildOnlyCommand() {
        return guildOnlyCommand;
    }

    protected void setGuildOnlyCommand(boolean guildOnlyCommand) {
        this.guildOnlyCommand = guildOnlyCommand;
    }

    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        if (!event.isFromGuild() && isGuildOnlyCommand())//no autocompletion if the command isn't supported
            return;
        List<String> options = new ArrayList<>();
        autoComplete(event.getFocusedOption().getName(), options);

        List<net.dv8tion.jda.api.interactions.commands.Command.Choice> choices = new ArrayList<>();
        String value = event.getFocusedOption().getValue().toLowerCase();
        for (String option : options) {
            if (choices.size() >= 25)//choices limited to 25
                break;
            if (value.length() <= 100 && option.toLowerCase().contains(value))
                choices.add(new net.dv8tion.jda.api.interactions.commands.Command.Choice(option, option));
        }
        event.replyChoices(choices).queue();
    }

    public void register(CommandHandler handler) {
        //do nothing
    }

    public void autoComplete(String option, List<String> options) {
        //do nothing by default
    }

    public Logger getLogger() {
        return logger;
    }

    public List<Permission> getSuggestedPermissions(CommandEvent event) {
        Guild guild = event.getGuild();
        if (guild == null)
            return Collections.emptyList();
        List<Permission> permissions = new ArrayList<>();
        for (Permission permission : getRequiredBotPermisions()) {
            if (!guild.getSelfMember().hasPermission(permission))
                permissions.add(permission);
        }
        return permissions;
    }
}
