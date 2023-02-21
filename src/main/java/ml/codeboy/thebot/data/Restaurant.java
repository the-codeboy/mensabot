package ml.codeboy.thebot.data;

public class Restaurant {
    public String name;
    private Rating rating = new Rating();
    private int price=-1;// price in cents for standard meal

    public String getName() {
        return name;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPriceString() {
        return (price==-1?"?":price)+"€";
    }
}
