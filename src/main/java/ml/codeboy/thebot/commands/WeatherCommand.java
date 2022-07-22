package ml.codeboy.thebot.commands;

import ml.codeboy.met.Weather4J;
import ml.codeboy.met.data.Forecast;
import ml.codeboy.openweathermap.data.Location;
import ml.codeboy.thebot.commands.image.ImageCommand;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.List;

import static ml.codeboy.thebot.WeatherUtil.generateForecastImage;

public class WeatherCommand extends ImageCommand {

    public WeatherCommand() {
        super("weather", "send forecast", "forecast");
        setHidden(false);
        setGuildOnlyCommand(false);
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.STRING, "city", "The city for the forecast");
    }

    @Override
    protected void generateImage(CommandEvent event) {
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

        List<Forecast> forecasts = Weather4J.getForecasts(lat, lon);
        Instant now = Instant.now();
        while (forecasts.get(1).getTime().isBefore(now)) {
            forecasts.remove(0);
        }

        BufferedImage image = generateForecastImage(forecasts, 16);
        event.reply("Data from The Norwegian Meteorological Institute", image, "png", "weather_forecast");
    }


}
