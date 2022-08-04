package ml.codeboy.thebot.commands.mensa;

import com.github.codeboy.api.Meal;
import ml.codeboy.thebot.CommandHandler;
import ml.codeboy.thebot.Config;
import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.commands.secret.AcceptImage;
import ml.codeboy.thebot.data.FoodRatingManager;
import ml.codeboy.thebot.data.MealImage;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

import java.util.UUID;

public class AddImageCommand extends Command {
    private final String selectMenuId = "add-image", acceptImageId = "accept-image", rejectImageId = "reject-image",
            rejectImageModalId = "reject-image-modal";
    private CommandHandler commandHandler;

    public AddImageCommand() {
        super("addImage", "add a meal image");
        setGuildOnlyCommand(false);
    }

    @Override
    public void register(CommandHandler handler) {
        this.commandHandler = handler;
    }

    @Override
    public SlashCommandData getCommandData() {
        return null;
    }

    @Override
    public void run(CommandEvent event) {
        if (event.isSlashCommandEvent())
            return;
        MessageReceivedEvent e = event.getMessageReceivedEvent();
        if (e.getMessage().getAttachments().isEmpty()) {
            event.replyError("Please send the image you want to add with the comment " + Config.getInstance().prefix + getName());
        }
        String name = String.join(" ", event.getArgs());
        String url = e.getMessage().getAttachments().get(0).getProxyUrl();

        if (name.length() > 0) {
            addImage(e.getJDA(), name, url, e.getAuthor());
        } else {
            String id = UUID.randomUUID() + selectMenuId;
            SelectMenu.Builder builder = SelectMenu.create(id);
            builder.setRequiredRange(1, 1);
            for (Meal meal : event.getDefaultMensa().getMeals()) {
//                String emoji = MensaUtil.getEmojiForMeal(meal);
                try {
                    builder.addOption(meal.getName(), meal.getName());
                } catch (IllegalArgumentException ex) {//can not add more options
                    break;
                }
            }
            commandHandler.registerSelectMenuListener(id, ev -> {
                if (!ev.getComponentId().equals(id)) {
                    return false;//this is not the right menu
                }
                String n = ev.getInteraction().getSelectedOptions().get(0).getValue();
                addImage(ev.getJDA(), n, url, event.getUser());
                ev.getMessage().delete().queue();
                return true;
            });
            e.getChannel().sendMessage("please select the meal this image is for")
                    .setActionRow(builder.build()).queue();
        }
    }

    private void addImage(JDA jda, String name, String url, User author) {
        TextChannel channel = (TextChannel) jda.getGuildChannelById(Config.getInstance().dmDebugChannel);
        MealImage image = FoodRatingManager.getInstance().addImage(name, url, author);
        if (channel != null) {
            String acceptId = image.getId() + acceptImageId;
            String rejectId = image.getId() + rejectImageId;
            commandHandler.registerButtonListener(acceptId, e -> {
                e.deferEdit().queue();
                if (Config.getInstance().admins.contains(e.getMember().getId())) {
                    AcceptImage.accept(image, e.getChannel());
                    return true;
                }
                return false;
            });
            commandHandler.registerButtonListener(rejectId, e -> {
                e.deferEdit().queue();
                if (Config.getInstance().admins.contains(e.getMember().getId())) {
                    String modalId = image.getId() + rejectImageModalId;
                    TextInput reason = TextInput.create("reason", "Reason", TextInputStyle.SHORT)
                            .setPlaceholder("The reason for rejecting this image")
                            .build();

                    Modal modal = Modal.create(modalId, "Reject Image")
                            .addActionRows(ActionRow.of(reason))
                            .build();

                    commandHandler.registerModalListener(modalId, ev -> {
                        image.reject(ev.getValue("reason").getAsString());
                        ev.reply("Image rejected").setEphemeral(true).queue();
                        return true;
                    });
                    e.replyModal(modal).queue();
                    return true;
                }
                return false;
            });
            channel.sendMessage(author.getAsTag() + " added image for " + name + " with id " + image.getId() + "\n" + url)
                    .setActionRow(Button.primary(acceptId, "accept"), Button.danger(rejectId, "reject")).queue();
        }
        author.openPrivateChannel().flatMap(c -> c.sendMessage("Your image for " + name + " has been submitted. I will let you know when it gets accepted")).queue();
    }
}
