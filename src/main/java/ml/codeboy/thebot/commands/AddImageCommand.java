package ml.codeboy.thebot.commands;

import com.github.codeboy.api.Meal;
import ml.codeboy.thebot.CommandHandler;
import ml.codeboy.thebot.Config;
import ml.codeboy.thebot.SelectMenuListener;
import ml.codeboy.thebot.data.FoodRatingManager;
import ml.codeboy.thebot.data.MealImage;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

import java.util.HashMap;

public class AddImageCommand extends Command implements SelectMenuListener {
    private final HashMap<User, String> uploadedImages = new HashMap<>();
    private final String selectMenuId = "add-image";
    private CommandHandler commandHandler;

    public AddImageCommand() {
        super("addImage", "add a meal image");
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
            String id = e.getAuthor().getId() + selectMenuId;
            uploadedImages.put(e.getAuthor(), url);
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
            commandHandler.registerSelectMenuListener(id, this);
            e.getChannel().sendMessage("please select the meal this image is for")
                    .setActionRow(builder.build()).queue();
        }
    }

    private void addImage(JDA jda, String name, String url, User author) {
        TextChannel channel = (TextChannel) jda.getGuildChannelById(Config.getInstance().dmDebugChannel);
        MealImage image = FoodRatingManager.getInstance().addImage(name, url, author);
        if (channel != null) {
            channel.sendMessage(author.getAsTag() + " added image for " + name + " with id " + image.getId() + "\n" + url).queue();
        }
        author.openPrivateChannel().flatMap(c -> c.sendMessage("Your image for " + name + " has been submitted. I will let you know when it gets accepted")).queue();
    }

    @Override
    public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
        event.deferEdit().queue();
        if (!event.getComponentId().equals(event.getUser().getId() + selectMenuId)) {
            return;//this is not their menu
        }
        String url = uploadedImages.remove(event.getUser());
        if (url == null) {
            event.getChannel().sendMessage("Please upload an image first using " + Config.getInstance().prefix + getName()).queue();
            return;
        }
        String name = event.getInteraction().getSelectedOptions().get(0).getValue();
        addImage(event.getJDA(), name, url, event.getUser());
        event.getMessage().delete().queue();
    }
}
