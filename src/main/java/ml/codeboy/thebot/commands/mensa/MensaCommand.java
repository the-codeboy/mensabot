package ml.codeboy.thebot.commands.mensa;

import com.github.codeboy.OpenMensa;
import com.github.codeboy.api.Meal;
import com.github.codeboy.api.Mensa;
import ml.codeboy.thebot.CommandHandler;
import ml.codeboy.thebot.util.MensaUtil;
import ml.codeboy.thebot.apis.RWTHMensa;
import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.data.*;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.util.Replyable;
import ml.codeboy.thebot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

import java.awt.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MensaCommand extends Command {

    public MensaCommand() {
        super("mensa", "Sends the current food in mensa Academica", "food");
        OpenMensa.getInstance().reloadCanteens();//doesn't work without this
        Collection<Mensa> mensas = OpenMensa.getInstance().searchMensa("aachen");

        for (Mensa mensa : mensas) {
            OpenMensa.getInstance().addMensa(new RWTHMensa(mensa));
        }
        setGuildOnlyCommand(false);
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData()
                .addOption(OptionType.INTEGER, "mensa_id", "The id of the mensa eg 187", false)
                .addOption(OptionType.STRING, "date", "The date", false, true)
                .addOption(OptionType.STRING, "name", "Name of the mensa", false, true);
    }

    @Override
    public void autoComplete(String option, List<String> options) {
        switch (option) {
            case "date": {
                options.add("gestern");
                options.add("heute");
                options.add("morgen");
                options.add("übermorgen");
                break;
            }
            case "name": {
                for (Mensa mensa : OpenMensa.getInstance().getAllCanteens()) {
                    if (mensa != null)
                        options.add(mensa.getName());
                }
                break;
            }
        }
    }

    @Override
    public void run(CommandEvent event) {
        String[] args = event.getArgs();
        if (args.length == 0) {
            sendDefaultMenu(event);
        } else if (event.isMessageEvent()) {
            Mensa mensa;

            try {
                int mensaId = Integer.parseInt(args[0]);

                mensa = OpenMensa.getInstance().getMensa(mensaId);
            } catch (NumberFormatException e) {
                mensa = tryGetMensa(event, args[0]);
                if (mensa == null)
                    return;
            }

            Date date = MensaUtil.wordToDate(args[1]);
            if (date == null) {
                event.replyError("Invalid argument: " + args[1] + ". Expected one of: yesterday, today, tomorrow");
            } else {
                sendMensaMenu(event, mensa, date);
            }

        } else {
            SlashCommandInteractionEvent sce = event.getSlashCommandEvent();
            Mensa mensa = GuildManager.getInstance().getData(event.getGuild()).getDefaultMensa();
            Date date = new Date();
            for (OptionMapping om : sce.getOptions()) {
                if (om.getName().equals("mensa_id")) {
                    mensa = OpenMensa.getInstance().getMensa((int) om.getAsLong());
                } else if (om.getName().equals("date")) {
                    date = MensaUtil.wordToDate(om.getAsString());
                    if (date == null) {
                        event.replyError("Invalid argument: " + om.getAsString() + ". Expected one of: yesterday, today, tomorrow");
                        return;
                    }
                } else if (om.getName().equals("name")) {
                    mensa = tryGetMensa(event, om.getAsString());
                    if (mensa == null)
                        return;
                }
            }
            sendMensaMenu(event, mensa, date);
        }
    }

    private Mensa tryGetMensa(CommandEvent event, String query) {
        List<Mensa> mensas = OpenMensa.getInstance().searchMensa(query);
        if (mensas.size() == 0) {
            event.replyError("No mensas found matching " + query);
        } else if (mensas.size() == 1) {
            return mensas.get(0);
        } else {
            sendMensas(event, mensas, query);
        }
        return null;
    }

    private void sendMensas(CommandEvent event, List<Mensa> mensas, String query) {
        EmbedBuilder builder = event.getBuilder();

        builder.setTitle("Mensas matching " + query);

        int defaultMensaId = event.getGuildData().getDefaultMensaId();

        for (Mensa mensa : mensas) {
            builder.addField(mensa.getName(), "id: " + mensa.getId() + (mensa.getId() == defaultMensaId ? "\nThis servers default mensa" : "")
                    , true);
            if (!builder.isValidLength()) {
                builder.getFields().remove(builder.getFields().size() - 1);

                event.reply(builder);
                return;
            }
        }

        event.reply(builder);
    }

    private void sendDefaultMenu(CommandEvent event) {
        Mensa mensa;
        if (event.getGuild() == null)
            mensa = OpenMensa.getInstance().getMensa(187);
        else
            mensa = GuildManager.getInstance().getData(event.getGuild()).getDefaultMensa();
        sendMensaMenu(event, mensa);
    }

    private void sendMensaMenu(CommandEvent event, Mensa mensa) {
        sendMensaMenu(event, mensa, new Date());
    }

    private void sendMensaMenu(CommandEvent event, Mensa mensa, Date date) {
        if (!mensa.isOpen(date)) {
            event.replyError("The mensa " + mensa.getName() + " is not open " + MensaUtil.dateToWord(date));
            return;
        }
        EmbedBuilder builder = MensaUtil.MealsToEmbed(mensa, date);
        ActionRow mealButtons = MensaUtil.createMealButtons(mensa, date);
        if (event.isSlashCommandEvent()) {
            event.getSlashCommandEvent().getInteraction().getHook().sendMessageEmbeds(builder.build()).addActionRows(mealButtons).queue();
        } else if (event.isMessageEvent())
            event.getMessageReceivedEvent().getChannel().sendMessageEmbeds(builder.build()).setActionRows(mealButtons).queue();
    }

    @Override
    public void register(CommandHandler handler) {
        super.register(handler);
        getInteractionHandler().registerRegexButtonListener(this::rate, "^rate.*");
        getInteractionHandler().registerRegexSelectMenuListener(this::rate, "^rate.*");
        getInteractionHandler().registerRegexButtonListener(this::detail, "^detail.*");
        getInteractionHandler().registerRegexSelectMenuListener(this::detail, "^detail.*");
    }

    private boolean rate(ButtonInteractionEvent event) {
        event.deferEdit().queue();
        String componentId = event.getComponentId();
        String[] args = componentId.split(":");
        if (args.length == 1) {
            event.getHook().sendMessage("This button is not working anymore :(").setEphemeral(true).queue();
            return false;
        }
        int mensaId = Integer.parseInt(args[1]);
        String date = args[2];
        Mensa mensa = OpenMensa.getInstance().getMensa(mensaId);

        SelectMenu.Builder builder = SelectMenu.create("rate").setRequiredRange(1, 1);

        for (Meal meal : mensa.getMeals(date)) {
            try {
                boolean usedTwice = false;
                for (SelectOption o : builder.getOptions())
                    if (o.getValue().equals(meal.getName())) {
                        usedTwice = true;
                        break;// wieso gibt es am selben tag zweimal das gleiche essen???
                    }
                if (!usedTwice)
                    builder.addOption(meal.getName(), meal.getName());
            } catch (Exception ignored) {
            }
        }

        event.getInteraction().getHook().sendMessageEmbeds(new EmbedBuilder().setTitle("Which meal do you want to rate?").build())
                .setEphemeral(true)
                .addActionRow(builder.build()).queue();
        return false;
    }

    private boolean rate(SelectMenuInteractionEvent event) {
        event.deferEdit().queue();
        String meal = event.getSelectedOptions().get(0).getValue();
        String id = UUID.randomUUID().toString();
        SelectMenu.Builder builder = SelectMenu.create(id).setRequiredRange(1, 1);

        for (int i = 1; i < 6; i++) {
            builder.addOption(Util.repeat("⭐", i), i + "");
        }
        getInteractionHandler().registerSelectMenuListener(id, e -> {
            openRatingModal(e, meal);

            User user = event.getUser();
            UserData data = UserDataManager.getInstance().getData(user);
            int rating = Integer.parseInt(e.getSelectedOptions().get(0).getValue());
            data.addRating(meal, rating);

            e.getInteraction().getHook().sendMessageEmbeds(new EmbedBuilder().setTitle("Rating added: " + meal)
                    .addField(MensaUtil.getRatingString(rating) + " added",
                            MensaUtil.getRatingString(FoodRatingManager.getInstance().getRating(meal)) + " (" + FoodRatingManager.getInstance().getRatings(meal) + ") total",
                            true)
                    .setColor(Color.YELLOW)
                    .setFooter(e.getMember().getEffectiveName(), e.getMember().getUser().getEffectiveAvatarUrl())
                    .build()).queue();

            RateCommand.updateAllGuildAnnouncements();

            return true;
        });

        event.getInteraction().getHook().sendMessageEmbeds(new EmbedBuilder().setTitle("How many stars do you give " + meal + "?").build())
                .addActionRow(builder.build()).setEphemeral(true).queue();
        return false;
    }

    private void openRatingModal(SelectMenuInteractionEvent event, String meal) {
        String id = UUID.randomUUID().toString();

        getInteractionHandler().registerModalListener(id, e -> {
            e.deferReply(true).queue();
            String comment = e.getValue("comment").getAsString();
            CommentManager.getInstance().addComment(meal, comment, e.getUser());
            e.getHook().sendMessageEmbeds(new EmbedBuilder().setTitle("Added comment").setDescription(comment).build()).queue();
            return true;
        });

        event.replyModal(Modal.create(id, "Thanks for rating. You can put a comment here")//exactly the 45 chars limit XD
                .addActionRow(TextInput.create("comment", "Comment", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Put your comment here or click cancel if you don't want to comment")
                        .setRequiredRange(1, 1024)
                        .build()).build()).complete();
    }

    private boolean detail(ButtonInteractionEvent event) {
        event.deferEdit().queue();
        String componentId = event.getComponentId();
        String[] args = componentId.split(":");
        if (args.length == 1) {
            event.getHook().sendMessage("This button is not working anymore :(").setEphemeral(true).queue();
            return false;
        }
        int mensaId = Integer.parseInt(args[1]);
        String date = args[2];
        Mensa mensa = OpenMensa.getInstance().getMensa(mensaId);

        SelectMenu.Builder builder = SelectMenu.create(componentId).setRequiredRange(1, 1);

        for (Meal meal : mensa.getMeals(date)) {
            try {
                boolean usedTwice = false;
                for (SelectOption o : builder.getOptions())
                    if (o.getValue().equals(meal.getName())) {
                        usedTwice = true;
                        break;// wieso gibt es am selben tag zweimal das gleiche essen???
                    }
                if (!usedTwice)
                    builder.addOption(meal.getName(), meal.getName());
            } catch (Exception ignored) {
            }
        }

        event.getInteraction().getHook().sendMessageEmbeds(new EmbedBuilder().setTitle("Which meal do you want details for?").build())
                .setEphemeral(true)
                .addActionRow(builder.build()).queue();
        return false;
    }

    private boolean detail(SelectMenuInteractionEvent event) {
        event.deferEdit().queue();
        String componentId = event.getComponentId();
        String[] args = componentId.split(":");
        if (args.length == 1) {
            event.getHook().sendMessage("This button is not working anymore :(").setEphemeral(true).queue();
            return false;
        }
        int mensaId = Integer.parseInt(args[1]);
        String date = args[2];
        Mensa mensa = OpenMensa.getInstance().getMensa(mensaId);

        String name = event.getSelectedOptions().get(0).getValue();

        for (Meal meal : mensa.getMeals(date)) {
            if (meal.getName().equals(name)) {
                DetailCommand.sendDetails(Replyable.from(event), meal);
                return false;
            }
        }
        return false;
    }


}
