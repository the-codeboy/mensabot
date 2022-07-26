package ml.codeboy.thebot.data;

public class Restaurant {
    public String name;
    private Rating rating = new Rating();

    public String getName() {
        return name;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }
}
