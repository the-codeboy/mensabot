package ml.codeboy.thebot.data;

public class Rating {
    public static final int MAX_RATING=5;
    private int ratings = 0;
    private int total = 0;

    public double getAverage() {
        if(ratings==0)
            return -1;//no ratings
        return total / (double) ratings;
    }

    public void addRating(int rating) {
        ratings++;
        total += rating;
    }

    public void removeRating(int rating) {
        ratings--;
        total -= rating;
    }

    public int getRatings() {
        return ratings;
    }
}
