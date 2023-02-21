package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.MensaUtil;
import ml.codeboy.thebot.data.Restaurant;
import ml.codeboy.thebot.data.RestaurantManager;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Comparator;
import java.util.List;

public class Dönertop extends Command {
    public Dönertop() {
        super("dönertop", "best döner", "dt");
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder = event.getBuilder();
        builder.setTitle("Dönertop");
        List<Restaurant> restaurants = RestaurantManager.getInstance().getRestaurants();
        restaurants.sort(Comparator.comparing(Restaurant::getRating).reversed());
        for (Restaurant restaurant : restaurants) {
            builder.addField(restaurant.getName()+" ("+restaurant.getPriceString()+")",
                    MensaUtil.getRatingString(restaurant.getRating().getAverage()) + " (" + restaurant.getRating().getRatings() + ")", false);
        }
        event.reply(builder);
    }
}
