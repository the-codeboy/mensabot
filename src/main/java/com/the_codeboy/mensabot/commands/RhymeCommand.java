package com.the_codeboy.mensabot.commands;

import com.the_codeboy.mensabot.apis.RhymeApi;
import com.the_codeboy.mensabot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RhymeCommand extends Command {
    public RhymeCommand() {
        super("rhyme", "Gives you a word that rhymes with the one provided");
        setGuildOnlyCommand(false);
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData()
                .addOption(OptionType.STRING, "word"
                        , "The word to find rhymes for", true);
    }

    @Override
    public void run(CommandEvent event) {
        String[] args = event.getArgs();
        if (args.length == 0) {
            event.reply(newBuilder().setColor(Color.RED).setTitle("Error")
                    .setDescription("Please provide a word"));
        } else {
            String word = args[0];
            ArrayList<String> words = RhymeApi.getInstance().getRhymingWords(word);
            EmbedBuilder builder = newBuilder();
            builder.setTitle("Words that rhyme with " + word);
            for (String w : words) {
                if (!builder.isValidLength()) {
                    List<MessageEmbed.Field> fields = builder.getFields();
                    fields.remove(fields.size() - 1);
                    break;
                }
                builder.addField(w, "", true);
            }
            event.reply(builder);
        }
    }
}
