package com.the_codeboy.mensabot.commands;

import com.the_codeboy.mensabot.Bot;
import com.the_codeboy.mensabot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Help extends Command {
    private final Bot bot;

    public Help(Bot bot) {
        super("help", "gives help");
        this.bot = bot;
        setGuildOnlyCommand(false);
    }

    @Override
    public void run(CommandEvent event) {
        Collection<Command> commands = bot.getCmdHandler().getCommands();
        EmbedBuilder embedBuilder = newBuilder();
        embedBuilder.setTitle("Help for commands");
        List<MessageEmbed> embeds = new ArrayList<>();
        for (Command command : commands) {
            if (!command.isHidden())
                embedBuilder.addField(command.getName(), command.getDescription(), true);
            if (embedBuilder.getFields().size() >= 25) {
                embeds.add(embedBuilder.build());
                embedBuilder = newBuilder();
                embedBuilder.setTitle("Help page " + (embeds.size() + 1));
            }
        }
        if (!embedBuilder.getFields().isEmpty())
            embeds.add(embedBuilder.build());
        event.reply(embeds.toArray(new MessageEmbed[0]));
    }
}
