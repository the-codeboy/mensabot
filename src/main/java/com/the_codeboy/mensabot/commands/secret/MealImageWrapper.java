package com.the_codeboy.mensabot.commands.secret;

import com.the_codeboy.mensabot.data.MealImage;

public class MealImageWrapper extends MealImage {
    private final String meal;

    public MealImageWrapper(String author, String url, String meal) {
        super(author, url);
        this.meal = meal;
    }

    public static MealImageWrapper fromImage(MealImage image, String meal) {
        return new MealImageWrapper(image.getAuthor(), image.getUrl(), meal);
    }

    public String getMeal() {
        return meal;
    }
}
