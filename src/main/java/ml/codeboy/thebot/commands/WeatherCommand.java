package ml.codeboy.thebot.commands;

import ml.codeboy.met.Weather4J;
import ml.codeboy.met.data.Forecast;
import ml.codeboy.thebot.commands.image.ImageCommand;
import ml.codeboy.thebot.events.CommandEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WeatherCommand extends ImageCommand {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:00")
            .withZone(ZoneId.systemDefault());

    private final Color transparentYellow = new Color(255, 255, 0, 50);

    public WeatherCommand() {
        super("weather", "send forecast", "forecast");
        setHidden(false);
    }

    @Override
    protected void generateImage(CommandEvent event) {
        List<Forecast> forecasts = Weather4J.getForecasts("50.775345", "6.083887");
        Forecast forecast = forecasts.remove(0);
        Instant now = Instant.now();
        while (forecast.getTime().isBefore(now)) {
            forecast = forecasts.remove(0);
        }
        forecasts.add(0, forecast);

        BufferedImage image = generateForecastImage(forecasts, 16);
        event.reply("Data from The Norwegian Meteorological Institute", image, "png", "weather_forecast");
    }

    private BufferedImage generateForecastImage(List<Forecast> forecasts, int values) {
        final int size = 200, space = size / 5;
        BufferedImage image = new BufferedImage((space + size) * values, 6 * size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setColor(new Color(54, 57, 63));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());

        double min = forecasts.get(0).getAirTemperature(), max = min;
        for (int i = 1; i < values; i++) {
            double temp = forecasts.get(i).getAirTemperature();
            if (temp < min)
                min = temp;
            if (temp > max)
                max = temp;
        }

        Forecast last = null;
        for (int i = 0; i < values; i++) {
            Forecast forecast = forecasts.get(i);

            double lastTemp = forecast.getAirTemperature();
            if (last != null) {
                lastTemp = last.getAirTemperature();
            }
            last = forecast;

            int startY = (int) (size * 3 + space - size * 2 * (lastTemp - min) / (max - min));
            int endY = (int) (size * 3 + space - size * 2 * (forecast.getAirTemperature() - min) / (max - min));

            g.setColor(Color.ORANGE);

            int startX = (i - 1) * (size + space), endX = i * (size + space);
            g.drawLine(startX, startY, endX, endY);

            g.setColor(transparentYellow);

            g.fillPolygon(new int[]{startX, endX, endX, startX}, new int[]{startY, endY, image.getHeight(), image.getHeight()}, 4);

            if (i == values - 1) {
                g.setColor(Color.ORANGE);
                startX = i * (size + space);
                endX = (i + 1) * (size + space);

                g.drawLine(startX, endY, endX, endY);

                g.setColor(transparentYellow);

                g.fillPolygon(new int[]{startX, endX, endX, startX}, new int[]{endY, endY, image.getHeight(), image.getHeight()}, 4);
            }

            g.setColor(Color.BLACK);
            int x = i * (size + space) - space / 2;
            if (i != 0)
                g.drawLine(x, 0, x, image.getHeight());

            drawString(g, formatter.format(forecast.getTime()), new Rectangle((size + space) * i, 4 * size, size, size), Color.WHITE);
            drawString(g, forecast.getAirTemperature() + "°", new Rectangle((size + space) * i, 3 * size, size, size), Color.WHITE);
            try {
                Image symbol = ImageIO.read(new File("images/weather/png/" + forecast.getSymbol() + ".png"));

                g.drawImage(symbol, (size + space) * i, space, size, size, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        g.dispose();
        return image;
    }
}
