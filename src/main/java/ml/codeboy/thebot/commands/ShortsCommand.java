package ml.codeboy.thebot.commands;

import ml.codeboy.openweathermap.OpenWeatherApi;
import ml.codeboy.openweathermap.data.ApiResponse;
import ml.codeboy.openweathermap.data.HourlyForecast;
import ml.codeboy.thebot.Config;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.IOException;

public class ShortsCommand extends Command {
    private final OpenWeatherApi weather4J;

    public ShortsCommand() {
        super("ShouldIWearShortsToday", "Lets you know if you should wear shorts today", "shorts");
        weather4J = new OpenWeatherApi(Config.getInstance().openWeatherApiKey);
    }

    @Override
    public void run(CommandEvent event) {
        ApiResponse response = null;
        try {
            response = weather4J.getForecast("50.775345", "6.083887", "metric", "de");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int sunrise = response.getCity().getSunrise();
        int sunset = response.getCity().getSunset();
        int lowestTempForShorts = 15;
        float lowestTempToday = 100;
        boolean shortsWeather = true;
        for (HourlyForecast hf : response.getList()) {
            if (hf.getDt() > sunrise && hf.getDt() < sunset) {
                if (hf.getMain().getTemp_min() < lowestTempToday) {
                    lowestTempToday = hf.getMain().getFeels_like();
                }
            }
        }
        shortsWeather = lowestTempToday > lowestTempForShorts;
        EmbedBuilder builder = event.getBuilder();
        builder.setColor(shortsWeather ? Color.GREEN : Color.RED);
        builder.setTitle(shortsWeather ? "It is shorts weather today" : "It isn't shorts weather today");
        event.reply(builder);
    }
}
