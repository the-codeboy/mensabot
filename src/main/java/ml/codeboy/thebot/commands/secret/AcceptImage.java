package ml.codeboy.thebot.commands.secret;

import ml.codeboy.thebot.data.FoodRatingManager;
import ml.codeboy.thebot.data.MealImage;
import ml.codeboy.thebot.data.UserData;
import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.UUID;

public class AcceptImage extends SecretCommand {
    public AcceptImage() {
        super("accept", "");
    }

    public static void accept(MealImage image, MessageChannel channel) {
        image.setAccepted(true);
        FoodRatingManager.getInstance().saveImages();
        channel.sendMessage("accepted " + image.getUrl()).queue();
        UserData data = UserDataManager.getInstance().getData(image.getAuthor());
        data.setKarma(data.getKarma() + 5);
        UserDataManager.getInstance().save(data);
        User user = channel.getJDA().getUserById(image.getAuthor());
        if (user != null) {
            user.openPrivateChannel().complete()
                    .sendMessage("Your image has been accepted. Thank you for your contribution. You received 5 karma for this").queue();
        }
    }

    @Override
    public void run(CommandEvent event) {
        String id = event.getArgs()[0];
        MealImage image = FoodRatingManager.getInstance().getImage(UUID.fromString(id));
        if (image == null)
            event.replyError("can not find image");
        else {
            accept(image, event.getChannel());
        }
    }
}
