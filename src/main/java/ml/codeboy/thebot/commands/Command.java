package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;

public abstract class Command {
    private final String name, description;
    private final String[] aliases;
    private Permission[] requiredPermisions = new Permission[0];
    private boolean hidden = false;

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

    public void execute(CommandEvent event) {
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
        return member.hasPermission(getRequiredPermisions());
    }

    public abstract void run(CommandEvent event);

    public CommandData getCommandData() {
        return new CommandData(getName().toLowerCase(), getDescription());
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
}
