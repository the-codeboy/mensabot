package com.the_codeboy.mensabot.commands.mensa;

import com.github.codeboy.api.Meal;
import com.github.codeboy.api.Mensa;
import com.the_codeboy.mensabot.commands.secret.AcceptImage;
import com.the_codeboy.mensabot.Config;
import com.the_codeboy.mensabot.commands.Command;
import com.the_codeboy.mensabot.data.FoodRatingManager;
import com.the_codeboy.mensabot.data.GuildManager;
import com.the_codeboy.mensabot.data.MealImage;
import com.the_codeboy.mensabot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddImageCommand extends Command {
    private final String selectMenuId = "add-image", acceptImageId = "accept-image", rejectImageId = "reject-image",
            rejectImageModalId = "reject-image-modal";
    private final int maxDaysAgo = 2;

    public AddImageCommand() {
        super("addImage", "add a meal image");
        setGuildOnlyCommand(false);
    }


    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.ATTACHMENT, "image", "The image you want to add", true)
                .addOption(OptionType.STRING, "meal", "The meal this image is for", true, true);
    }


    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent event) {
        List<String> options = new ArrayList<>();
        String selected = event.getFocusedOption().getName();


        if (selected.equals("meal")) {
            Mensa mensa = GuildManager.getInstance().getData(event.getGuild()).getDefaultMensa();
            for (int i = 0; i <= maxDaysAgo; i++) {
                Date date = new Date(System.currentTimeMillis() - 3600000L * 24 * i);
                for (Meal meal : mensa.getMeals(date)) {
                    if (meal.getName().length() <= 100)
                        options.add(meal.getName());
                }
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
        if (event.isSlashCommandEvent()) {
            SlashCommandInteraction sci = event.getSlashCommandEvent();
            String url = sci.getOption("image").getAsAttachment().getUrl();
            String meal = sci.getOption("meal").getAsString();
            addImage(sci.getJDA(), meal, url, event.getUser(), false);
            sci.getHook().sendMessageEmbeds(new EmbedBuilder().setTitle("Your image for " + meal + " has been submitted. I will let you know when it gets accepted").build()).queue();
            return;
        }
        MessageReceivedEvent e = event.getMessageReceivedEvent();
        if (e.getMessage().getAttachments().isEmpty()) {
            event.replyError("Please send the image you want to add with the comment " + Config.getInstance().prefix + getName());
        }
        String name = String.join(" ", event.getArgs());
        String url = e.getMessage().getAttachments().get(0).getProxyUrl();

        if (name.length() > 0) {
            addImage(e.getJDA(), name, url, e.getAuthor(), true);
        } else {
            String id = UUID.randomUUID() + selectMenuId;
            SelectMenu.Builder builder = SelectMenu.create(id);
            builder.setRequiredRange(1, 1);
            for (Meal meal : event.getDefaultMensa().getMeals()) {
//                String emoji = MensaUtil.getEmojiForMeal(meal);
                try {
                    builder.addOption(meal.getName(), meal.getName());
                } catch (IllegalArgumentException ignored) {
                }
            }
            getInteractionHandler().registerSelectMenuListener(id, ev -> {
                if (!ev.getComponentId().equals(id)) {
                    return false;//this is not the right menu
                }
                String n = ev.getInteraction().getSelectedOptions().get(0).getValue();
                addImage(ev.getJDA(), n, url, event.getUser(), true);
                ev.getMessage().delete().queue();
                return true;
            });
            e.getChannel().sendMessageEmbeds(new EmbedBuilder().setTitle("please select the meal this image is for").build())
                    .setActionRow(builder.build()).queue();
        }
    }

    private void addImage(JDA jda, String name, String url, User author, boolean notify) {
        TextChannel channel = (TextChannel) jda.getGuildChannelById(Config.getInstance().dmDebugChannel);
        if (channel != null) {
            UUID id = UUID.randomUUID();
            String acceptId = id + acceptImageId;
            String rejectId = id + rejectImageId;

            try {
                URL imageUrl = new URL(url);
                Message msg=channel.sendMessage(author.getAsTag() + " added image for " + name + " with id " + id + "\n" + url)
                        .addFile(imageUrl.openStream(),name+".png")
                        .setActionRow(Button.primary(acceptId, "accept"), Button.danger(rejectId, "reject")).complete();
                url=msg.getAttachments().get(0).getUrl();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            MealImage image = FoodRatingManager.getInstance().addImage(name, url, author);

            getInteractionHandler().registerButtonListener(acceptId, e -> {
                e.deferEdit().queue();
                if (Config.getInstance().admins.contains(e.getMember().getId())) {
                    AcceptImage.accept(image, e.getChannel());
                    return true;
                }
                return false;
            });
            getInteractionHandler().registerButtonListener(rejectId, e -> {
                if (Config.getInstance().admins.contains(e.getMember().getId())) {
                    String modalId = image.getId() + rejectImageModalId;
                    TextInput reason = TextInput.create("reason", "Reason", TextInputStyle.SHORT)
                            .setPlaceholder("The reason for rejecting this image")
                            .build();

                    Modal modal = Modal.create(modalId, "Reject Image")
                            .addActionRows(ActionRow.of(reason))
                            .build();

                    getInteractionHandler().registerModalListener(modalId, ev -> {
                        image.reject(ev.getValue("reason").getAsString());
                        ev.reply("Image rejected").setEphemeral(true).queue();
                        return true;
                    });
                    e.replyModal(modal).queue();
                    return true;
                }
                return false;
            });
        }else {
            FoodRatingManager.getInstance().addImage(name, url, author);// add without having to manually accept
        }
        if (notify)
            author.openPrivateChannel().flatMap(c -> c.sendMessage("Your image for " + name + " has been submitted. I will let you know when it gets accepted")).queue();
    }
}
