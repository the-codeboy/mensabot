package com.the_codeboy.mensabot;

import com.github.codeboy.Util;
import com.github.codeboy.api.Meal;
import com.github.codeboy.api.Mensa;
import com.the_codeboy.mensabot.data.CommentManager;
import com.the_codeboy.mensabot.data.EmojiManager;
import com.the_codeboy.mensabot.data.FoodRatingManager;
import com.the_codeboy.mensabot.data.MealEmoji;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.the_codeboy.mensabot.commands.image.ImageCommand.drawString;

public class MensaUtil {
    public static ActionRow createMealButtons(Mensa mensa, Date date) {
        String dataString = ":" + mensa.getId() + ":" + Util.dateToString(date);
        return ActionRow.of(Button.primary("rate" + dataString, Emoji.fromFormatted("⭐")),
                Button.secondary("detail" + dataString, "details"));
    }

    public static EmbedBuilder MealsToEmbed(Mensa mensa, Date date) {
        EmbedBuilder builder = new EmbedBuilder();
        if (!mensa.isOpen(date) || mensa.getMeals(date).isEmpty()) {
            builder.setTitle(mensa.getName() + " is closed " + dateToWord(date));
            return builder;
        }
        builder.setTitle("Meals in " + mensa.getName());
        if (mensa.hasOpeningHours()) {
            float openTime = mensa.getOpeningTime(date),
                    closeTime = mensa.getClosingTime(date);
            Date openDate = dateAtTime(date, openTime),
                    closeDate = dateAtTime(date, closeTime);

            builder.setDescription("<t:" + openDate.getTime() / 1000 + ":R> - <t:" + closeDate.getTime() / 1000 + ":R>");
        } else {
            builder.setDescription(DayOfWeek.of(date.getDay() == 0 ? 7 : date.getDay()).getDisplayName(TextStyle.FULL, Locale.GERMANY) + ", " + Util.dateToString(date));
        }
        boolean beilagen = false;
        for (Meal meal : mensa.getMeals(date)) {
            String title = getTitleString(meal);
            String description = meal.getCategory() +
                    (meal.getPrices().getStudents() != null ? "\n" + toPrice(meal.getPrices().getStudents())
                            + (meal.getPrices().getOthers() != null ? " (" + toPrice(meal.getPrices().getOthers()) + ")" : "") : "");

            if (!beilagen
                    && (meal.getCategory().equalsIgnoreCase("Hauptbeilagen") || meal.getCategory().equalsIgnoreCase("Nebenbeilage"))) {
                beilagen = true;
                builder.addBlankField(false);
            }
            boolean inline = true;
            builder.addField(title, description,
                    inline);
        }

        return builder;
    }

    private static Date dateAtTime(Date date, float time) {
        int hourPart = (int) time;
        int minutePart = (int) ((time - hourPart) * 60);

        // Use Calendar to set the time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // Set the date to the calendar
        calendar.set(Calendar.HOUR_OF_DAY, hourPart);
        calendar.set(Calendar.MINUTE, minutePart);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static String getTitleString(Meal meal) {
        String title = getEmojiForMeal(meal);
        title += " " + meal.getName();
        String additionalEmojis = getAdditionalEmojisString(meal);
        double rating = FoodRatingManager.getInstance().getRating(meal.getName());
        String ratingString = getRatingString(rating);
        String ratingInfo = "";// empty by default
        if (rating != -1) {
            ratingInfo = " (" + FoodRatingManager.getInstance().getRatings(meal.getName()) + ")";
        }
        int length = title.length() + additionalEmojis.length() + ratingString.length() + ratingInfo.length();
        if (length >= 256) {// make sure the title is not too long
            ratingString = getRatingString(rating, true);// use shorter rating string
            if (title.length() + additionalEmojis.length() + ratingString.length() + ratingInfo.length() >= 256) {
                title = title.substring(0, 256 - additionalEmojis.length() - 3) + "...";// only shorten title if it is still too long
            }
        }
        title += additionalEmojis + ratingString + ratingInfo;
        return title;
    }

    public static String getAdditionalEmojisString(Meal meal) {
        String string = " ";
        if (meal.getNotes().contains("vegan")) {
            string += "<:vegan:1003629202739822702>";
        } else if (meal.getNotes().contains("vegetarisch") || meal.getCategory().toLowerCase().contains("vegetarisch")) {
            string += "<:vegetarian:1003629571104591923>";
        }
        if (FoodRatingManager.getInstance().getImage(meal.getName()).length() > 0)
            string += "\uD83D\uDCF7";//camera emoji
        if (!CommentManager.getInstance().getComments(meal.getName()).isEmpty())
            string += "\uD83D\uDCAC";//emoji :speech_baloon:
        return string;
    }

    public static String getRatingString(double rating) {
        return getRatingString(rating, false);
    }

    public static String getRatingString(double rating, boolean shorter) {
        StringBuilder title = new StringBuilder();
        if (rating != -1) {
            title.append("\n");
            while (rating >= 1) {
                title.append(shorter ? "⭐" : "<:star:992412997886693476>");// the shorter version is platform dependent so it should be avoided
                rating--;
            }
            if (rating > 0.9)
                title.append("<:09:982648330666528769>");
            else if (rating > 0.8)
                title.append("<:08:982648332801441852>");
            else if (rating > 0.7)
                title.append("<:07:982648330666528769>");
            else if (rating > 0.6)
                title.append("<:06:982648332801441852>");
            else if (rating > 0.5)
                title.append("<:05:982648334621736960>");
            else if (rating > 0.4)
                title.append("<:04:982648321132855315>");
            else if (rating > 0.3)
                title.append("<:03:982648322944819280>");
            else if (rating > 0.2)
                title.append("<:02:982648324228268084>");
            else if (rating > 0.1)
                title.append("<:01:982648326103134319>");
        }
        return title.toString();
    }

    private static String toPrice(String f) {
        float value = Float.parseFloat(f);
        NumberFormat currencyFormatter =
                NumberFormat.getCurrencyInstance(Locale.GERMANY);
        return currencyFormatter.format(value).replace(" ", "");
    }

    public static String getEmojiForMeal(Meal meal) {
        String name = meal.getName();

        MealEmoji emoji = getEmojiForWord(name);
        for (String text : meal.getNotes()) {
            MealEmoji newEmoji = getEmojiForWord(text);
            if (newEmoji != null && (emoji == null || newEmoji.getPriority() > emoji.getPriority()))
                emoji = newEmoji;
        }

        if (emoji != null)
            return emoji.getEmoji();

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
            default:
                return ":fork_knife_plate:";
        }
    }

    public static MealEmoji getEmojiForWord(String word) {
        word = word.toLowerCase();

        return EmojiManager.getInstance().getMatching(word);
    }

    public static String dateToWord(Date date) {
        long seconds = (date.getTime() - System.currentTimeMillis()) / 1000;
        long twelvehours = 60 * 60 * 12;
        if (Math.abs(seconds) < twelvehours) {
            return "today";
        }
        if (seconds > 0) {
            if (seconds < 3 * twelvehours) {
                return "tomorrow";
            }
            int days = (int) (1 + (seconds - twelvehours) / twelvehours);
            return "in " + days + " days";
        }
        if (seconds > -3 * twelvehours) {
            return "yesterday";
        }
        int days = (int) (1 - (seconds + twelvehours) / twelvehours);
        return days + " days ago";
    }

    public static Date wordToDate(String word) {
        word = word.toLowerCase();
        long dayinmili = 1000 * 60 * 60 * 24;
        if (word.equals("today") || word.equals("heute"))
            return new Date();
        if (word.equals("yesterday") || word.equals("gestern"))
            return new Date(System.currentTimeMillis() - dayinmili);
        if (word.equals("tomorrow") || word.equals("morgen"))
            return new Date(System.currentTimeMillis() + dayinmili);
        if (word.endsWith("morgen")) {
            int i = 1;
            while (word.startsWith("über")) {
                i++;
                word = word.substring(4);
            }
            if (word.equals("morgen")) {
                return new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * i);
            }
        }
        if (word.endsWith("gestern")) {
            int i = 1;
            while (word.startsWith("vor")) {
                i++;
                word = word.substring(3);
            }
            if (word.equals("gestern")) {
                return new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * i);
            }
        }
        return null;
    }

    public static BufferedImage generateMealsImage(Mensa mensa, Date date) {
        int width = 1080, height = 1350;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);

        g.fillRect(0, 0, width, height);

        int size = 50, space = size / 10;

        drawString(g, mensa.getName(), new Rectangle(space, space, width - space, size * 2 - space));
        //2*size

        List<Meal> meals = mensa.getMeals(date);

        int heightOfMeals = 800;
        int heightPerMeal = heightOfMeals * 2 / meals.size();

        for (int i = 0; i < meals.size(); i++) {
            boolean secondColumn = i >= meals.size() / 2;
            Meal meal = meals.get(i);
            int id = secondColumn ? i - meals.size() / 2 : i;

            Rectangle rectangle = new Rectangle((secondColumn ? width / 2 : 0) + space, size * 3 + id * heightPerMeal, width / 2 - 2 * space, heightPerMeal / 4);
            drawString(g, meal.getName(), rectangle);

            String description = meal.getCategory() +
                    (meal.getPrices().getStudents() != null ? "\n " + toPrice(meal.getPrices().getStudents())
                            + (meal.getPrices().getOthers() != null ? " (" + toPrice(meal.getPrices().getOthers()) + ")" : "") : "");

            rectangle.y += size;

            drawString(g, description, rectangle);

            rectangle.y += size;

            int ratings = FoodRatingManager.getInstance().getRatings(meal.getName());
            if (ratings > 0) {
                String rating = FoodRatingManager.getInstance().getRating(meal.getName()) + "/5 (" + ratings + ")";
                drawString(g, rating, rectangle);
            }
        }


        g.dispose();
        return image;
    }

    public static void display(BufferedImage image) {
        JFrame frame = new JFrame() {
            @Override
            public void paint(Graphics g) {
                g.drawImage(image, 0, 0, g.getClipBounds().width, g.getClipBounds().height, null);
            }
        };
        frame.setUndecorated(true);
        frame.setSize(image.getWidth() / 2, image.getHeight() / 2);
        frame.setVisible(true);
    }

}
