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
import org.mariuszgromada.math.mxparser.Expression;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Util {

    private static final Random rand = new Random();
    private static final Color[] colors = new Color[]{Color.blue, Color.cyan, Color.magenta, Color.orange, Color.pink, Color.yellow, Color.white};
    private static final String arrow = "↑";

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

    public static EmbedBuilder getSongInfo(AudioTrack track, EmbedBuilder builder) {
        builder.setTitle(track.getInfo().title);
        builder.setDescription(" by " + track.getInfo().author);
        builder.addField("Duration", Util.toReadable(track.getPosition()) + "/" + Util.toReadable(track.getDuration()), true);
        builder.addField("Time left: " + Util.toReadable(track.getDuration() - track.getPosition() - 1),  //subtract one millisecond extra because songs weirdly always end one millisecond early
                Util.getProgress(20, (double) track.getPosition() / track.getDuration(), ":white_large_square:", ":black_large_square:"), false);
        return builder;
    }    private static final int secondsPerDay = 3600 * 24,
            secondsPerMonth = secondsPerDay * 30, secondsPerYear = secondsPerDay * 365;

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

    public static double evaluate(String text) {
        try {
            Expression e = new Expression(text);
            return e.calculate();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return true if the text contains ↑ meaning it is probably in knuths arrow notation
     */
    public static boolean isKAN(String text) {
        return text.contains(arrow);
    }

    public static BigInteger getKANValue(String text) {
        int i = text.indexOf(arrow), j = text.lastIndexOf(arrow);
        int a = Integer.parseInt(text.substring(0, i)), b = Integer.parseInt(text.substring(j + 1));
        int arrows = j - i + 1;
        return kanValueInternal(BigInteger.valueOf(a), arrows, b);
    }

    private static BigInteger kanValueInternal(BigInteger a, int b, int c) {
        System.out.println(a + " " + b + " " + c);
        if (c > 1 && b > 1)
            return kanValueInternal(a, b - 1, kanValueInternal(a, b, c - 1).intValueExact());
        return a.pow(c);
    }

    public static String toDigits(int maxDigits, String text) {
        if (text.length() > maxDigits) {
            int originalLength = text.length();
            text = text.substring(0, maxDigits - 10);
            text += "E" + (originalLength - text.length());
        }
        return text;
    }

    public static int calculateDistance(String from, String to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("can't get the distance for null");
        }
        int sourceLength = from.length();
        int targetLength = to.length();
        if (sourceLength == 0) return targetLength;
        if (targetLength == 0) return sourceLength;
        int[][] dist = new int[sourceLength + 1][targetLength + 1];
        for (int i = 0; i < sourceLength + 1; i++) {
            dist[i][0] = i;
        }
        for (int j = 0; j < targetLength + 1; j++) {
            dist[0][j] = j;
        }
        for (int i = 1; i < sourceLength + 1; i++) {
            for (int j = 1; j < targetLength + 1; j++) {
                int cost = from.charAt(i - 1) == to.charAt(j - 1) ? 0 : 1;
                dist[i][j] = Math.min(Math.min(dist[i - 1][j] + 1, dist[i][j - 1] + 1), dist[i - 1][j - 1] + cost);
                if (i > 1 &&
                        j > 1 &&
                        from.charAt(i - 1) == to.charAt(j - 2) &&
                        from.charAt(i - 2) == to.charAt(j - 1)) {
                    dist[i][j] = Math.min(dist[i][j], dist[i - 2][j - 2] + cost);
                }
            }
        }
        return dist[sourceLength][targetLength];
    }




}
