package ml.codeboy.thebot.events;

import com.github.codeboy.OpenMensa;
import com.github.codeboy.api.Mensa;
import ml.codeboy.thebot.Config;
import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.commands.sound.GuildMusicManager;
import ml.codeboy.thebot.commands.sound.PlayerManager;
import ml.codeboy.thebot.data.GuildData;
import ml.codeboy.thebot.data.GuildManager;
import ml.codeboy.thebot.util.Replyable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public abstract class CommandEvent implements Replyable {
    private static final Random random = new Random();
    private final Event jdaEvent;
    private boolean ephermal = false;
    private final Command command;

    public CommandEvent(Event jdaEvent, Command command) {
        this.jdaEvent = jdaEvent;
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public abstract void reply(String message);

    public abstract void reply(MessageEmbed... embed);

    public abstract void reply(String message, File file, String name);

    public abstract User getUser();

    public abstract Member getMember();

    public void reply(EmbedBuilder builder) {
        reply(builder.build());
    }


    public abstract void edit(String message);

    public abstract void edit(MessageEmbed... embed);

    public void edit(EmbedBuilder builder) {
        edit(builder.build());
    }

    public abstract void reply(File file, String name);

    public void reply(BufferedImage image, String type) {
        reply(image, type, "image");
    }

    public void reply(BufferedImage image, String type, String name) {
        reply(null, image, type, name);
    }

    public void reply(String message, BufferedImage image, String type) {
        reply(message, image, type, "image");
    }

    public void reply(String message, BufferedImage image, String type, String name) {
        File file = new File("images/" + random.nextInt() + "." + type);
        try {
            ImageIO.write(image, type, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (message == null)
            reply(file, name + "." + type);
        else reply(message, file, name + "." + type);
        file.delete();
    }

    public void replyError(String error) {
        reply(new EmbedBuilder().setTitle("Error").setDescription(error).setColor(Color.RED).build());
    }

    //region getter
    public MessageReceivedEvent getMessageReceivedEvent() {
        return (MessageReceivedEvent) jdaEvent;
    }

    public SlashCommandInteractionEvent getSlashCommandEvent() {
        return (SlashCommandInteractionEvent) jdaEvent;
    }

    public String[] getArgs() {// TODO: Add better way to use arguments. Maybe something similar to JDAs slash commands
        if (isMessageEvent()) {// this will never happen since MessageCommandEvent overrides this method
            String content = getMessageReceivedEvent().getMessage().getContentRaw();
            content = content.replaceFirst(Config.getInstance().prefix, "");

            String[] splitContent = content.split(" ", 2);

            if (splitContent.length == 1)
                return new String[0];

            String withoutCommand = splitContent[1];
            return withoutCommand.split(" ");
        } else {
            ArrayList<String> arguments = new ArrayList<>();
            for (OptionMapping o : getSlashCommandEvent().getOptions()) {
                arguments.add(o.getAsString());
            }
            return arguments.toArray(new String[0]);
        }
    }

    public Event getJdaEvent() {
        return jdaEvent;
    }

    public boolean isMessageEvent() {
        return this instanceof MessageCommandEvent;
    }

    public boolean isSlashCommandEvent() {
        return this instanceof SlashCommandCommandEvent;
    }

    public abstract Guild getGuild();

    public GuildData getGuildData() {
        return GuildManager.getInstance().getData(getGuild());
    }

    public abstract MessageChannel getChannel();

    public Message send(String message) {
        return getChannel().sendMessage(message).complete();
    }

    public Message send(MessageEmbed message) {
        return getChannel().sendMessageEmbeds(message).complete();
    }

    public Message send(EmbedBuilder builder) {
        return send(builder.build());
    }

    public void replyErrorUnknown() {
        replyError("unknown error");
    }

    public EmbedBuilder getBuilder() {
        return new EmbedBuilder();
    }

    public GuildMusicManager getManager() {
        return PlayerManager.getInstance().getMusicManager(getGuild());
    }

    public boolean isEphermal() {
        return ephermal;
    }

    public void setEphermal(boolean ephermal) {
        this.ephermal = ephermal;
    }

    public Mensa getDefaultMensa() {
        if (getGuild() == null)
            return OpenMensa.getInstance().getMensa(187);
        return getGuildData().getDefaultMensa();
    }

    public JDA getJDA() {
        return getJdaEvent().getJDA();
    }

    //endregion
}
