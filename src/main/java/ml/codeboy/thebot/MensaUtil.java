package ml.codeboy.thebot;

import com.github.codeboy.Util;
import com.github.codeboy.api.Meal;
import com.github.codeboy.api.Mensa;
import ml.codeboy.thebot.data.FoodRatingManager;
import net.dv8tion.jda.api.EmbedBuilder;

import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

public class MensaUtil {
    public static EmbedBuilder MealsToEmbed(Mensa mensa, Date date) {
        EmbedBuilder builder = new EmbedBuilder();
        if (!mensa.isOpen(date) || mensa.getMeals(date).isEmpty()) {
            builder.setTitle(mensa.getName() + " is closed " + dateToWord(date));
            return builder;
        }
        builder.setTitle("Meals in " + mensa.getName());
        builder.setDescription(DayOfWeek.of(date.getDay() == 0 ? 7 : date.getDay()).getDisplayName(TextStyle.FULL, Locale.GERMANY) + ", " + Util.dateToString(date));
        for (Meal meal : mensa.getMeals(date)) {
            String symbol = getEmojiForMeal(meal);
            String description = meal.getCategory() +
                    (meal.getPrices().getStudents() != null ? "\n" + toPrice(meal.getPrices().getStudents())
                            + " (" + toPrice(meal.getPrices().getOthers()) + ")" : "");
            String title = symbol + " " + meal.getName();
            int rating = (int) FoodRatingManager.getInstance().getRating(meal.getName());
            if (rating != -1) {
                title += " ";
                for (int i = 0; i < rating; i++) {
                    title += ":star:";
                }
            }
            boolean inline = !(meal.getCategory().equalsIgnoreCase("Hauptbeilagen") || meal.getCategory().equalsIgnoreCase("Nebenbeilage"));
            builder.addField(title, description,
                    inline);
        }

        return builder;
    }

    private static String toPrice(String f) {
        float value = Float.parseFloat(f);
        NumberFormat currencyFormatter =
                NumberFormat.getCurrencyInstance(Locale.GERMANY);
        return currencyFormatter.format(value).replace(" ", "");
    }

    private static String getEmojiForMeal(Meal meal) {
        String name = meal.getName().toLowerCase();

        if (name.contains("schnitzel"))
            return "<:schnitzel:943559144135336047>";
        if (name.contains("burger"))
            return ":hamburger:";
        if (name.contains("pizza"))
            return ":pizza:";
        if (name.contains("pfannkuchen"))
            return ":pancakes:";
        if (name.contains("kuchen") || name.contains("küchlein"))
            return ":cake:";
        if (name.contains("spaghetti"))
            return ":spaghetti:";
        if (name.contains("suppe"))
            return ":stew:";
        if (name.contains("chili") || name.contains("scharf"))
            return ":hot_pepper:";
        if (name.contains("keule"))
            return ":poultry_leg:";
        if (name.contains("steak"))
            return ":cut_of_meat:";
        if (name.contains("hähnchen") || name.contains("huhn"))
            return ":chicken:";
        if (name.contains("fisch") || name.contains("lachs"))
            return ":fish:";
        if (name.contains("reis"))
            return ":rice:";
        if (name.contains("pommes"))
            return ":fries:";
        if (name.contains("apfel"))
            return ":apple:";
        if (name.contains("brokkoli"))
            return ":broccoli:";
        if (name.contains("paprika"))
            return ":bell_pepper:";
        if (name.contains("mais"))
            return ":corn:";
        if (name.contains("karotte"))
            return ":carrot:";
        if (name.contains("kartoffel"))
            return ":potato:";
        if (name.contains("salat"))
            return ":salad:";
        if (name.contains("eintopf"))
            return ":stew:";
        if (name.contains("käse"))
            return ":cheese:";
        if (name.contains("zwiebel"))
            return ":onion:";

        switch (meal.getCategory()) {
            case "Vegetarisch":
                return ":leafy_green:";
            case "Klassiker":
                return ":cut_of_meat:";
            case "Burger der Woche":
            case "Burger Classics":
                return ":hamburger:";
            case "Hauptbeilagen":
                return ":potato:";
            case "Nebenbeilage":
                return ":salad:";
        }
        return ":fork_knife_plate:";
    }

    public static String dateToWord(Date date) {
        long seconds = (date.getTime() - System.currentTimeMillis()) / 1000;
        if (Math.abs(seconds) < (60 * 60 * 12)) {
            return "today";
        }
        if (seconds > 0) {
            if (seconds < 3 * 60 * 60 * 12) {
                return "tomorrow";
            }
            int days = (int) (1 + (seconds - 60 * 60 * 12) / (60 * 60 * 24));
            return "in " + days + " days";
        }
        if (seconds > -3 * 60 * 60 * 12) {
            return "yesterday";
        }
        int days = (int) (1 - (seconds + 60 * 60 * 12) / (60 * 60 * 24));
        return days + " days ago";
    }

    public static Date wordToDate(String word) {
        if (word.equalsIgnoreCase("today"))
            return new Date();
        if (word.equalsIgnoreCase("yesterday"))
            return new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        if (word.equalsIgnoreCase("tomorrow"))
            return new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        return null;
    }
}
