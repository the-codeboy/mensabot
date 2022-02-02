package ml.codeboy.thebot.util;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

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
    }    private static final int secondsPerDay = 3600 * 24,
            secondsPerMonth = secondsPerDay * 30, secondsPerYear = secondsPerDay * 365;

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
//                    System.out.println("cancelled current track display");
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

    public static Member getAsMember(User user){
        if(user.getMutualGuilds().isEmpty())
            return null;
        return user.getMutualGuilds().get(0).getMember(user);
    }


}
