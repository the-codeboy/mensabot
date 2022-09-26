package ml.codeboy.thebot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ml.codeboy.thebot.data.UserData;
import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Util {

    public static String toReadable(long millis) {
        int seconds = (int) (millis / 1000);
        int years = seconds / secondsPerYear;
        seconds -= years * secondsPerYear;
        int months = seconds / secondsPerMonth;
        seconds -= months * secondsPerMonth;
        int days = seconds / secondsPerDay;
        seconds -= days * secondsPerDay;
        int hours = seconds / 3600;
        seconds -= hours * 3600;
        int minutes = seconds / 60;
        seconds -= minutes * 60;

        String date = "";
        if (years > 0)
            date += years + " year" + (years > 1 ? "s " : " ");
        if (months > 0)
            date += months + " month" + (months > 1 ? "s " : " ");
        if (days > 0)
            date += days + " day" + (days > 1 ? "s " : " ");
        if (hours > 0)
            date += hours + " hour" + (hours > 1 ? "s " : " ");
        if (minutes > 0)
            date += minutes + " minute" + (minutes > 1 ? "s " : " ");
        if (seconds > 0)
            date += seconds + " second" + (seconds > 1 ? "s " : " ");
        if (date.length() == 0)
            date = millis + " milliseconds";
        return date;
    }

    public static String repeat(String s, int times) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++) {
            builder.append(s);
        }
        return builder.toString();
    }

    public static String getProgress(int total, double progress, String first, String second) {
        int repeatFirst = (int) (0.5 + total * progress), repeatSecond = total - repeatFirst;
        String output = repeat(first, repeatFirst) + repeat(second, repeatSecond);
        if (repeatSecond == 0)
            output += ":white_check_mark:";
        return output;
    }

    private static final int secondsPerDay = 3600 * 24,
            secondsPerMonth = secondsPerDay * 30, secondsPerYear = secondsPerDay * 365;

    public static EmbedBuilder getSongInfo(AudioTrack track, EmbedBuilder builder) {
        builder.setTitle(track.getInfo().title);
        builder.setDescription(" by " + track.getInfo().author);
        builder.addField("Duration", Util.toReadable(track.getPosition()) + "/" + Util.toReadable(track.getDuration()), true);
        builder.addField("Time left: " + Util.toReadable(track.getDuration() - track.getPosition() - 1),  //subtract one millisecond extra because songs weirdly always end one millisecond early
                Util.getProgress(20, (double) track.getPosition() / track.getDuration(), ":white_large_square:", ":black_large_square:"), false);
        return builder;
    }

    public static void sendTrackInfo(CommandEvent event, AudioTrack track) {
        AudioPlayer player = event.getManager().audioPlayer;
        Message message = event.send(Util.getSongInfo(track, event.getBuilder()));
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                message.editMessageEmbeds(Util.getSongInfo(track, event.getBuilder()).build()).complete();
                if (track.getPosition() >= track.getDuration() - 300 || player.getPlayingTrack() != track) {
                    cancel();
                }
            }
        }, 1000, 5000);
    }

    public static int toInt(String string) {
        return toInt(string, 0);
    }

    public static int toInt(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public static EmbedBuilder sign(EmbedBuilder builder, CommandEvent e) {
        builder.setFooter("requested by " + e.getMember().getEffectiveName(), e.getMember().getUser().getAvatarUrl());
        return builder;
    }

    public static Member getAsMember(User user) {
        if (user.getMutualGuilds().isEmpty())
            return null;
        return user.getMutualGuilds().get(0).getMember(user);
    }

    private static final Random rand = new Random();
    private static final Color[] colors = new Color[]{Color.blue, Color.cyan, Color.magenta, Color.orange, Color.pink, Color.yellow, Color.white};

    public static Color getRandomColor() {
//        float r = rand.nextFloat();
//        float g = rand.nextFloat();
//        float b = rand.nextFloat();
//        return new Color(r,g,b);
        return colors[rand.nextInt(colors.length)];
    }

    public static void addKarma(User user, int amount) {
        UserData data = UserDataManager.getInstance().getData(user);
        data.setKarma(data.getKarma() + amount);
        UserDataManager.getInstance().save(data);
    }

    public static BufferedImage getAvatarImage(User user) throws IOException {
        return getImageFromUrl(user.getEffectiveAvatarUrl());
    }

    public static BufferedImage getImageFromUrl(String adress) throws IOException {
        URL url = new URL(adress);
        URLConnection hc = url.openConnection();
        hc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        return ImageIO.read(hc.getInputStream());
    }

    public static BufferedImage ensureOpaque(BufferedImage bi) {
        if (bi.getTransparency() == BufferedImage.OPAQUE)
            return bi;
        int w = bi.getWidth();
        int h = bi.getHeight();
        int[] pixels = new int[w * h];
        bi.getRGB(0, 0, w, h, pixels, 0, w);
        BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        bi2.setRGB(0, 0, w, h, pixels, 0, w);
        return bi2;
    }

    public static <T> void shuffle(T[] a) {
        T tmp;
        int tmpInt = 0;
        for (int i = 0; i < a.length; i++) {
            tmp = a[i];
            tmpInt = rand.nextInt(a.length);
            a[i] = a[tmpInt];
            a[tmpInt] = tmp;
        }
    }


    public static void addSusCount(User user, int amount) {
        UserData data = UserDataManager.getInstance().getData(user);
        data.setSusCount(data.getSusCount() + amount);
        UserDataManager.getInstance().save(data);
    }

    public static String getRandomGif() {
        String url = "https://api.giphy.com/v1/gifs/random?api_key=0UTRbFtkMxAplrohufYco5IY74U8hOes";
        try {
            String response = com.github.codeboy.jokes4j.util.Util.get(new URL(url));
            JsonElement json = JsonParser.parseString(response);
            String string = json.getAsJsonObject().get("data").getAsJsonObject().get("url").getAsString();
            return string;
        } catch (Exception e) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            return getRandomGif();
        }
    }


    public static String readUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Weather4J");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine).append("\n");
            }

            String result = stringBuilder.toString();
            return result;
        }
    }
}
