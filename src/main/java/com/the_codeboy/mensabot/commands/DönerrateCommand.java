package com.the_codeboy.mensabot.commands;

import com.the_codeboy.mensabot.MensaUtil;
import com.the_codeboy.mensabot.data.Restaurant;
import com.the_codeboy.mensabot.data.RestaurantManager;
import com.the_codeboy.mensabot.data.UserDataManager;
import com.the_codeboy.mensabot.events.CommandEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.List;

public class DönerrateCommand extends Command {

    private final int maxDaysAgo = 2;

    public DönerrateCommand() {
        super("dönerrate", "Rate döner places");
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData()
                .addOption(OptionType.STRING, "restaurant", "The restaurant to rate", true, true)
                .addOption(OptionType.STRING, "rating", "The rating to give - a number between 1 and 5", true, true);
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        List<String> options = new ArrayList<>();
        String selected = event.getFocusedOption().getName();


        switch (selected) {
            case "restaurant": {
                for (Restaurant r : RestaurantManager.getInstance().getRestaurants())
                    options.add(r.getName());
                break;
            }
            case "rating": {
                options.add("1");
                options.add("2");
                options.add("3");
                options.add("4");
                options.add("5");
                break;
            }
        }

        List<net.dv8tion.jda.api.interactions.commands.Command.Choice> choices = new ArrayList<>();
        String value = event.getFocusedOption().getValue().toLowerCase();
        for (String option : options) {
            if (choices.size() >= 25)//choices limited to 25
                break;
            if (option.toLowerCase().contains(value)) {
                net.dv8tion.jda.api.interactions.commands.Command.Choice choice = new net.dv8tion.jda.api.interactions.commands.Command.Choice(option, option);
                if (!choices.contains(choice))
                    choices.add(choice);
            }
        }
        event.replyChoices(choices).queue();
    }

    @Override
    public void run(CommandEvent event) {
        if (event.isMessageEvent())
            event.replyError("Please use the slashcommand");
        else {
            SlashCommandInteractionEvent scie = event.getSlashCommandEvent();
            String restaurant = scie.getOption("restaurant").getAsString();
            int rating = scie.getOption("rating").getAsInt();
            if (rating <= 5 && rating >= 1) {
                boolean success = UserDataManager.getInstance().getData(event.getUser()).addRestaurantRating(restaurant, rating);
                Restaurant r = RestaurantManager.getInstance().getRestaurant(restaurant);
                if (!success) {
                    event.replyError("Failed to rate " + restaurant);
                    return;
                }
                event.reply("Rating added: " + restaurant + "\n"
                        + MensaUtil.getRatingString(rating) + " added \n"
                        + MensaUtil.getRatingString(r.getRating().getAverage())
                        + " (" + r.getRating().getRatings() + ") total");

            } else {
                event.reply("Invalid number. Has to be between 1 and 5");
            }
        }
    }
}
