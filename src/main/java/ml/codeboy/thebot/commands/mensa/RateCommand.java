package ml.codeboy.thebot.commands.mensa;

import com.github.codeboy.api.Meal;
import com.github.codeboy.api.Mensa;
import ml.codeboy.thebot.MensaBot;
import ml.codeboy.thebot.MensaUtil;
import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.data.FoodRatingManager;
import ml.codeboy.thebot.data.GuildData;
import ml.codeboy.thebot.data.GuildManager;
import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RateCommand extends Command {

    private final int maxDaysAgo = 2;

    public RateCommand() {
        super("rate", "Rate mensa meals");
    }

    public static void updateAllGuildAnnouncements() {
        JDA jda = MensaBot.getInstance().getJda();
        for (Guild guild : jda.getGuilds()) {
            GuildData data = GuildManager.getInstance().getData(guild);
            if (data != null)
                updateGuildAnnouncements(guild, jda);//update the meal announcements of the day to include the new rating
        }
    }

    private static void updateGuildAnnouncements(Guild guild, JDA jda) {
        GuildData data = GuildManager.getInstance().getData(guild);
        try {
            Mensa mensa = data.getDefaultMensa();
            MessageChannel channel = (MessageChannel) jda.getGuildChannelById(data.getUpdateChannelId());
            if (channel != null) {
                try {
                    Message message = channel.retrieveMessageById(data.getLatestAnnouncementId()).complete();
                    Date date=new Date(System.currentTimeMillis() + 1000 * 3600 * 4);
                    ActionRow mealButtons=MensaUtil.createMealButtons(mensa,date);
                    message.editMessageEmbeds(MensaUtil.MealsToEmbed(mensa, date).build())
                            .setActionRows(mealButtons).complete();
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
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
            if (value.length() <= 100 && option.toLowerCase().contains(value)) {
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
            String meal = scie.getOption("meal").getAsString();
            int rating = scie.getOption("rating").getAsInt();
            if (rating <= 5 && rating >= 1) {
                Mensa mensa = event.getGuildData().getDefaultMensa();
                boolean found = false;

                int i = 0;
                for (; i <= maxDaysAgo; i++) {
                    Date date = new Date(System.currentTimeMillis() - 3600000L * 24 * i);
                    found = mensa.getMeals(date).stream().anyMatch(m -> m.getName().equals(meal));
                    if (found)
                        break;
                }
                if (found) {//check if meal exists
                    UserDataManager.getInstance().getData(event.getUser()).addRating(meal, rating);
                    event.reply("Rating added: " + meal + "\n"
                            + MensaUtil.getRatingString(rating) + " added \n"
                            + MensaUtil.getRatingString(FoodRatingManager.getInstance().getRating(meal)) + " (" + FoodRatingManager.getInstance().getRatings(meal) + ") total");


                    if (i == 0)//the rated meal was from today
                        updateAllGuildAnnouncements();
                } else {
                    event.reply("Meal not found");
                }
            } else {
                event.reply("Invalid number. Has to be between 1 and 5");
            }
        }

    }
}
