package ml.codeboy.thebot.commands;

import com.github.codeboy.api.Meal;
import com.github.codeboy.api.Mensa;
import ml.codeboy.thebot.MensaUtil;
import ml.codeboy.thebot.data.EmojiManager;
import ml.codeboy.thebot.data.FoodRatingManager;
import ml.codeboy.thebot.data.GuildManager;
import ml.codeboy.thebot.data.MealEmoji;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DetailCommand extends Command {

    private final int maxDaysAgo = 2;

    public DetailCommand() {
        super("detail", "gives details about a meal", "meal");
        EmojiManager.getInstance();
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData()
                .addOption(OptionType.STRING, "meal", "The meal to rate", true, true);
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        List<String> options = new ArrayList<>();
        String selected = event.getFocusedOption().getName();


        switch (selected) {
            case "meal": {
                Mensa mensa = GuildManager.getInstance().getData(event.getGuild()).getDefaultMensa();
                for (int i = 0; i <= maxDaysAgo; i++) {
                    Date date = new Date(System.currentTimeMillis() - 3600000L * 24 * i);
                    for (Meal meal : mensa.getMeals(date)) {
                        options.add(meal.getName());
                    }
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
        String name = null;
        if (event.isSlashCommandEvent()) {
            SlashCommandInteractionEvent scie = event.getSlashCommandEvent();
            name = scie.getOption("meal").getAsString();
        }
        if (name == null)
            name = String.join(" ", event.getArgs());

        Mensa mensa = GuildManager.getInstance().getData(event.getGuild()).getDefaultMensa();
        for (int i = 0; i <= maxDaysAgo; i++) {
            Date date = new Date(System.currentTimeMillis() - 3600000L * 24 * i);
            for (Meal meal : mensa.getMeals(date)) {
                if (meal.getName().equalsIgnoreCase(name)) {
                    sendDetails(event, meal);
                    return;
                }
            }
        }
        event.replyError("Can not find meal");
    }

    private void sendDetails(CommandEvent event, Meal meal) {
        EmbedBuilder builder = event.getBuilder();
        builder.setTitle(MensaUtil.getTitleString(meal));
        String url = FoodRatingManager.getInstance().getImage(meal.getName());
        if (url != null && url.length() > 0)
            builder.setImage(url);
        builder.addBlankField(false);
        for (String note : meal.getNotes()) {
            MealEmoji emoji = MensaUtil.getEmojiForWord(note);
            builder.addField((emoji == null ? "" : emoji.getEmoji()) + " " + note, "", true);
        }
        event.reply(builder);
    }
}
