package com.the_codeboy.mensabot;

import com.github.codeboy.api.Mensa;
import com.the_codeboy.mensabot.data.GuildData;
import com.the_codeboy.mensabot.data.GuildManager;
import ml.codeboy.met.Weather4J;
import ml.codeboy.met.data.Forecast;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.the_codeboy.mensabot.WeatherUtil.generateForecastImage;

public class AnnouncementTracker {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final JDA jda;
    private static AnnouncementTracker instance;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static AnnouncementTracker getInstance() {
        return instance;
    }

    public static void registerAnnouncementTracker(JDA jda){
        if(instance!=null)
            throw new IllegalStateException("AnnouncementTracker already registered");
        instance = new AnnouncementTracker(jda);
    }

    private AnnouncementTracker(JDA jda) {
        this.jda = jda;
        registerAnnouncements();
    }


    private void registerAnnouncements() {
        scheduleAnnouncement(20, false); // Schedule announcement at 20:00
        scheduleAnnouncement(7, true);  // Schedule announcement at 07:00
    }

    private void scheduleAnnouncement(int targetHour, boolean includeWeather) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(targetHour).withMinute(0).withSecond(0).withNano(0);

        // If nextRun is before now, schedule it for the next day
        if (nextRun.isBefore(now)) {
            nextRun = nextRun.plusDays(1);
        }

        long initialDelay = ChronoUnit.SECONDS.between(now, nextRun);
        executorService.scheduleAtFixedRate(() -> {
            sendMealsToAllGuilds();
            if (includeWeather) {
                sendWeatherToAllGuilds();
            }
        }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }

    private void sendMealsToGuild(GuildData data, Message message) {
        try {
            MessageChannel channel = (MessageChannel) jda.getGuildChannelById(data.getUpdateChannelId());
            if (channel != null) {
                message = channel.sendMessage(message).complete();
                data.setLatestAnnouncementId(message.getId());
                data.save();
            }
        } catch (Exception ignored) {
        }
    }

    public void sendMealsToAllGuilds() {
        logger.info("Sending meals to guilds");
        List<GuildData> data = GuildManager.getInstance().getAllGuildData();
        while (!data.isEmpty()) {
            GuildData d = data.remove(0);
            Mensa mensa = d.getDefaultMensa();
            Date date = new Date(System.currentTimeMillis() + 1000 * 3600 * 5);
            ActionRow mealButtons = MensaUtil.createMealButtons(mensa, date);
            Message message = new MessageBuilder()
                    .setEmbeds(MensaUtil.MealsToEmbed(mensa, date).build())
                    .setActionRows(mealButtons).build();
            sendMealsToGuild(d, message);
            data.removeIf(g -> {
                if (g.getDefaultMensaId() == d.getDefaultMensaId()) {
                    sendMealsToGuild(g, message);
                    return true;
                }
                return false;
            });
        }
    }

    private void sendWeatherToGuild(Guild guild, File file) {
        GuildData data = GuildManager.getInstance().getData(guild);
        try {
            Mensa mensa = data.getDefaultMensa();
            MessageChannel channel = (MessageChannel) jda.getGuildChannelById(data.getUpdateChannelId());
            if (channel != null) {

                channel.sendMessage(
                                "Forecast for " + mensa.getCity() + "\nData from The Norwegian Meteorological Institute")
                        .addFile(file, "weather_forecast.png").complete();
            }
        } catch (Exception ignored) {
        }
    }

    private void sendWeatherToAllGuilds() {
        logger.info("Sending weather to guilds");
        List<GuildData> data = GuildManager.getInstance().getAllGuildData();

        while (!data.isEmpty()) {
            GuildData d = data.remove(0);
            Mensa mensa = d.getDefaultMensa();

            List<Double> coordinates = mensa.getCoordinates();
            String lat = String.valueOf(coordinates.get(0)), lon = String.valueOf(coordinates.get(1));
            List<Forecast> forecasts = Weather4J.getForecasts(lat, lon);
            Instant now = Instant.now();
            while (forecasts.get(1).getTime().isBefore(now)) {
                forecasts.remove(0);
            }

            BufferedImage image = generateForecastImage(forecasts, 16);
            File file = new File("images/" + new Random().nextInt() + ".png");
            try {
                ImageIO.write(image, "png", file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            sendWeatherToGuild(d.getGuild(), file);
            data.removeIf(g -> {
                if (g.getDefaultMensaId() == d.getDefaultMensaId()) {
                    sendWeatherToGuild(g.getGuild(), file);
                    return true;
                }
                return false;
            });
            file.delete();
        }

    }


    public void sendAnnouncementToAllGuilds(Message message) {
        List<GuildData> data = GuildManager.getInstance().getAllGuildData();
        for (GuildData d : data) {
            try {
                MessageChannel channel = (MessageChannel) jda.getGuildChannelById(d.getUpdateChannelId());
                if (channel != null) {
                    channel.sendMessage(message).queue();
                }
            } catch (Exception ignored) {
            }
        }
    }
}
