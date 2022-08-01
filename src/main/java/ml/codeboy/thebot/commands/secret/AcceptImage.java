package ml.codeboy.thebot.commands.secret;

import ml.codeboy.thebot.data.FoodRatingManager;
import ml.codeboy.thebot.data.MealImage;
import ml.codeboy.thebot.events.CommandEvent;

import java.util.UUID;

public class AcceptImage extends SecretCommand {
    public AcceptImage() {
        super("accept", "");
    }

    @Override
    public void run(CommandEvent event) {
        String id = event.getArgs()[0];
        MealImage image = FoodRatingManager.getInstance().getImage(UUID.fromString(id));
        if (image == null)
            event.replyError("can not find image");
        else {
            image.setAccepted(true);
            FoodRatingManager.getInstance().saveImages();
            event.reply("accepted " + image.getUrl());
        }
    }
}
