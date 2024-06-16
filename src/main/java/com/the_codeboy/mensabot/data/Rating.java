package com.the_codeboy.mensabot.data;

import org.jetbrains.annotations.NotNull;

public class Rating implements Comparable<Rating> {
    public static final int MAX_RATING = 5;
    private int ratings = 0;
    private int total = 0;

    public double getAverage() {
        if (ratings == 0)
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

    @Override
    public int compareTo(@NotNull Rating o) {
        int result = Double.compare(getAverage(), o.getAverage());
        if (result == 0)
            result = Double.compare(getRatings(), o.getRatings());
        return result;
    }
}
