package com.the_codeboy.mensabot.commands;

import com.github.codeboy.jokes4j.Jokes4J;
import com.github.codeboy.jokes4j.api.*;
import com.the_codeboy.mensabot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.util.List;

public class JokeCommand extends Command {
    public JokeCommand() {
        super("joke", "tells a joke");
        setGuildOnlyCommand(false);
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.STRING, "category", "A specific category for a joke", false, true);
    }

    @Override
    public void autoComplete(String option, List<String> options) {
        if ("category".equals(option)) {
            for (Category category : Category.values()) {
                options.add(category.toString());
            }
        }
    }

    @Override
    public void run(CommandEvent event) {
        Joke joke = null;

        OptionMapping categoryMapping = event.isSlashCommandEvent() ? event.getSlashCommandEvent().getOption("category") : null;
        Category category = null;
        if (categoryMapping != null) {
            try {
                category = Category.valueOf(categoryMapping.getAsString());
            } catch (IllegalArgumentException e) {
                event.setEphermal(true);
                event.reply(event.getBuilder().setTitle("Joke - Error")
                        .setDescription("The given category is invalid. Please try again").setColor(Color.RED));
                return;
            }
        }
        Jokes4J instance = Jokes4J.getInstance();

        JokeRequest request = new JokeRequest.Builder()
                .categories(category)
                .blackList(Flag.explicit, Flag.nsfw, Flag.racist, Flag.sexist)
                .language(Language.ENGLISH)
                .build();

        joke=instance.getJoke(request);

        EmbedBuilder builder = event.getBuilder();
        if (joke.getType() == JokeType.single) {
            builder.setDescription(joke.getJoke());
        } else if (joke.getType() == JokeType.twopart) {
            builder.setTitle(joke.getSetup()).setDescription(joke.getDelivery());
        }
        event.reply(builder);
    }
}
