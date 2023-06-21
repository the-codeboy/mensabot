package ml.codeboy.thebot.tracker;

import ml.codeboy.thebot.Bot;
import ml.codeboy.thebot.data.UserData;
import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class BedTimeTracker {
    private static final SimpleDateFormat format = new SimpleDateFormat("hh:mm");
    private static final int millisPerDay = 1000 * 60 * 60 * 24;

    public BedTimeTracker(Bot bot) {
        scheduleReminders(bot);
    }

    private void scheduleReminders(Bot bot) {
        Timer timer = new Timer();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Berlin"));
        for (UserData data : UserDataManager.getInstance().getAllUserData()) {
            if (data.getBedTime() < 0)
                continue;
            User user = bot.getJda().retrieveUserById(data.getUserId()).complete();
            if (user == null)
                continue;
            int seconds = data.getBedTime();
            int hours = seconds / 3600;
            seconds -= hours * 3600;
            int minutes = seconds / 60;
            seconds -= minutes * 60;
            ZonedDateTime nextRun = now.withHour(hours).withMinute(minutes).withSecond(seconds);
            if (now.compareTo(nextRun) > 0)
                nextRun = nextRun.plusDays(1);

            Duration duration = Duration.between(now, nextRun);
            long delay = duration.getSeconds() * 1000;
            delay += 500;//make sure messages don't get sent too early
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Member member = Util.getAsMember(user);
                    if (member == null)
                        return;
                    if (member.getOnlineStatus() != OnlineStatus.OFFLINE
                        && member.getOnlineStatus() != OnlineStatus.UNKNOWN) {
                        remindOfBedtime(user);
                    }
                }
            }, delay);
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                scheduleReminders(bot);
            }
        }, millisPerDay);
    }

    private void remindOfBedtime(User user) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Time to go to bed").setColor(Color.RED)
                .setDescription("it is already " + format.format(new Date()));
        user.openPrivateChannel().flatMap(c -> c.sendMessageEmbeds(builder.build())).queue();
    }
}
