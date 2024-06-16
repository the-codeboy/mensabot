package com.the_codeboy.mensabot.commands.secret;

import com.the_codeboy.mensabot.data.FoodRatingManager;
import com.the_codeboy.mensabot.data.MealImage;
import com.the_codeboy.mensabot.events.CommandEvent;

import java.util.UUID;

public class RejectImage extends SecretCommand {
    public RejectImage() {
        super("reject", "");
    }

    @Override
    public void run(CommandEvent event) {
        String[] args = event.getArgs();
        String id = args[0];
        MealImage image = FoodRatingManager.getInstance().getImage(UUID.fromString(id));
        if (image == null)
            event.replyError("can not find image");
        else {
            String message = "";
            if (args.length > 1) {
                message = String.join(" ", args);
                message = message.substring(id.length() + 1);
            }
            image.reject(message);
            event.reply("rejected " + image.getUrl());
        }
    }
}
