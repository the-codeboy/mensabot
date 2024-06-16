package com.the_codeboy.mensabot.data;

import com.the_codeboy.mensabot.MensaBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.UUID;

public class MealImage {
    private final String author;
    private final String url;
    private final UUID id = UUID.randomUUID();
    private boolean accepted = false;

    public MealImage(String author, String url) {
        this.author = author;
        this.url = url;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public UUID getId() {
        return id;
    }

    public void accept() {
        setAccepted(true);
        FoodRatingManager.getInstance().saveImages();
        UserData data = UserDataManager.getInstance().getData(getAuthor());
        data.setKarma(data.getKarma() + 5);
        UserDataManager.getInstance().save(data);
        User user = MensaBot.getInstance().getJda().getUserById(getAuthor());
        if (user != null) {
            user.openPrivateChannel().complete()
                    .sendMessageEmbeds(
                            new EmbedBuilder().setTitle("Your image has been accepted")
                                    .setThumbnail(getUrl())
                                    .setDescription("Thank you for your contribution. You received 5 karma for this")
                                    .setColor(Color.GREEN)
                                    .build()
                    ).queue();
        }
    }

    public void reject(String message) {
        setAccepted(false);
        FoodRatingManager.getInstance().removeImage(this);
        User user = MensaBot.getInstance().getJda().getUserById(getAuthor());
        if (user != null) {
            user.openPrivateChannel().complete()
                    .sendMessageEmbeds(
                            new EmbedBuilder().setTitle("Your image has been rejected")
                                    .setThumbnail(getUrl())
                                    .setDescription(message)
                                    .setColor(Color.RED)
                                    .build()
                    ).queue();
        }
    }
}
