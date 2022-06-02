package ml.codeboy.thebot.commands;

import com.github.codeboy.api.Meal;
import ml.codeboy.thebot.data.FoodRatingManager;
import ml.codeboy.thebot.data.GuildManager;
import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.ArrayList;
import java.util.List;

public class RateCommand extends Command {

    public RateCommand() {
        super("rate", "rate mensa meals");
        FoodRatingManager.getInstance();//todo remove debug statement
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData()
                .addOption(OptionType.STRING, "meal", "The meal to rate", true, true)
                .addOption(OptionType.STRING, "rating", "The rating to give - a number between 1 and 5", true, true);
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        List<String> options = new ArrayList<>();
        String selected = event.getFocusedOption().getName();


        switch (selected) {
            case "meal": {
                for (Meal meal : GuildManager.getInstance().getData(event.getGuild()).getDefaultMensa().getMeals()) {
                    options.add(meal.getName());
                }
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
            if (option.toLowerCase().contains(value))
                choices.add(new net.dv8tion.jda.api.interactions.commands.Command.Choice(option, option));
        }
        event.replyChoices(choices).queue();
    }

    @Override
    public void run(CommandEvent event) {
        if (event.isMessageEvent())
            event.replyError("Please use the slashcommand");
        else {
            SlashCommandInteractionEvent scie = event.getSlashCommandEvent();
            String meal = scie.getOption("meal").getAsString();
            int rating = scie.getOption("rating").getAsInt();
            if (GuildManager.getInstance().getData(event.getGuild()).getDefaultMensa().getMeals().stream().anyMatch(m -> m.getName().equals(meal))) {//check if meal exists
                UserDataManager.getInstance().getData(event.getUser()).addRating(meal, rating);
                event.getChannel().sendMessage("rating added").queue();
            } else
            {
                event.getChannel().sendMessage("meal not found").queue();
            }
        }
    }
}
