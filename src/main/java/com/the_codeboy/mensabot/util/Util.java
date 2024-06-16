package com.the_codeboy.mensabot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.the_codeboy.mensabot.data.UserData;
import com.the_codeboy.mensabot.data.UserDataManager;
import net.dv8tion.jda.api.entities.User;
import org.mariuszgromada.math.mxparser.Expression;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Util {

    private static final Random rand = new Random();
    private static final Color[] colors = new Color[]{Color.blue, Color.cyan, Color.magenta, Color.orange, Color.pink, Color.yellow, Color.white};
    private static final String arrow = "↑";

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

    public static int toInt(String string, int defaultValue) {
        try {
            return Integer.parseInt(string);
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    public static Color getRandomColor() {
        return colors[rand.nextInt(colors.length)];
    }

    public static void addKarma(User user, int amount) {
        UserData data = UserDataManager.getInstance().getData(user);
        data.setKarma(data.getKarma() + amount);
        UserDataManager.getInstance().save(data);
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
