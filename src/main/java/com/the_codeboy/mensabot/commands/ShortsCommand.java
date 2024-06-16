package com.the_codeboy.mensabot.commands;

import ml.codeboy.openweathermap.OpenWeatherApi;
import ml.codeboy.openweathermap.data.ApiResponse;
import ml.codeboy.openweathermap.data.HourlyForecast;
import ml.codeboy.openweathermap.data.Location;
import com.the_codeboy.mensabot.Config;
import com.the_codeboy.mensabot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class ShortsCommand extends Command {
    private static OpenWeatherApi openWeather;

    public ShortsCommand() {
        super("ShouldIWearShortsToday", "Lets you know if you should wear shorts today", "shorts");
        openWeather = new OpenWeatherApi(Config.getInstance().openWeatherApiKey);
        setGuildOnlyCommand(false);
    }

    public static OpenWeatherApi getOpenWeather() {
        return openWeather;
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.STRING, "city", "The city you are in");
    }

    @Override
    public void run(CommandEvent event) {
        ApiResponse response = null;
        List<Double> coordinates = event.getDefaultMensa().getCoordinates();
        String lat = String.valueOf(coordinates.get(0)), lon = String.valueOf(coordinates.get(1));

        if (event.getArgs().length > 0) {
            String city = String.join(" ", event.getArgs());
            Location loc = ShortsCommand.getOpenWeather().getLocation(city);
            lat = String.valueOf(loc.getLat());
            lon = String.valueOf(loc.getLon());
        } else if (event.isSlashCommandEvent()) {
            OptionMapping option = event.getSlashCommandEvent().getOption("city");
            if (option != null) {
                String city = option.getAsString();
                Location loc = ShortsCommand.getOpenWeather().getLocation(city);
                lat = String.valueOf(loc.getLat());
                lon = String.valueOf(loc.getLon());
            }
        }
        try {
            response = openWeather.getForecast(lat, lon, "metric", "de");
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
