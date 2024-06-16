package com.the_codeboy.mensabot.commands;

import com.github.codeboy.piston4j.api.Piston;
import com.github.codeboy.piston4j.api.Runtime;
import com.the_codeboy.mensabot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.LinkedList;

public class LanguagesCommand extends Command {
    public LanguagesCommand() {
        super("languages", "sends available programming languages (see execute command)");
        setGuildOnlyCommand(false);
    }

    @Override
    public void run(CommandEvent event) {
        LinkedList<MessageEmbed> embeds = new LinkedList<>();
        EmbedBuilder builder = new EmbedBuilder();
        for (Runtime r : Piston.getDefaultApi().getRuntimes()) {
            if (builder.getFields().size() >= 25) {
                embeds.add(builder.build());
                builder = new EmbedBuilder();
            }
            StringBuilder s = new StringBuilder();
            String[] aliases = r.getAliases();
            for (int i = 0; i < aliases.length; i++) {
                String alias = aliases[i];
                s.append(alias);
                if (i < aliases.length - 1)
                    s.append(", ");
            }
            builder.addField(r.getLanguage(), s.toString(), true);
        }
        embeds.add(builder.build());
        event.reply(embeds.toArray(new MessageEmbed[0]));
    }
}
