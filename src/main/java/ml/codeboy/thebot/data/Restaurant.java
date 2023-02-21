package ml.codeboy.thebot.data;

import java.text.NumberFormat;
import java.util.Locale;

public class Restaurant {
    public String name;
    private Rating rating = new Rating();
    private double price=-1;// price in euro for standard meal

    public String getName() {
        return name;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPriceString() {
        return (price==-1?"?": NumberFormat.getCurrencyInstance(Locale.GERMANY).format(price));
    }
}
