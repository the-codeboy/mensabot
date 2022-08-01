package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.Config;
import ml.codeboy.thebot.data.FoodRatingManager;
import ml.codeboy.thebot.data.MealImage;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class AddImageCommand extends Command {
    public AddImageCommand() {
        super("addImage", "add a meal image");
    }

    @Override
    public SlashCommandData getCommandData() {
        return null;
    }

    @Override
    public void run(CommandEvent event) {
        MessageReceivedEvent e = event.getMessageReceivedEvent();
        String name = String.join(" ", event.getArgs());
        String url = e.getMessage().getAttachments().get(0).getProxyUrl();

        TextChannel channel = (TextChannel) event.getJdaEvent().getJDA().getGuildChannelById(Config.getInstance().dmDebugChannel);
        MealImage image = FoodRatingManager.getInstance().addImage(name, url, e.getAuthor());
        if (channel != null) {
            channel.sendMessage(e.getAuthor().getAsTag() + " added image for " + name + " with id " + image.getId() + "\n" + url).queue();
        }
    }
}
