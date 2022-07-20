package ml.codeboy.thebot.apis;

import com.github.codeboy.api.Meal;
import com.github.codeboy.api.Mensa;

import java.util.Date;
import java.util.List;

public class MensaVeganika implements Mensa {
    private final Mensa mensa;

    public MensaVeganika(Mensa mensa) {
        this.mensa = mensa;
    }


    @Override
    public List<Meal> getMeals() {
        return mensa.getMeals();
    }

    @Override
    public List<Meal> getMeals(Date date) {
        return mensa.getMeals(date);
    }

    @Override
    public List<Meal> getMeals(String date) {
        return mensa.getMeals(date);
    }

    @Override
    public boolean isOpen() {
        return mensa.isOpen();
    }

    @Override
    public boolean isOpen(Date date) {
        return mensa.isOpen(date);
    }

    @Override
    public boolean isOpen(String date) {
        return mensa.isOpen(date);
    }

    @Override
    public int getId() {
        return mensa.getId();
    }

    @Override
    public String getName() {
        return mensa.getName().replace("Academica", "Veganika");
    }

    @Override
    public String getCity() {
        return mensa.getCity();
    }

    @Override
    public String getAddress() {
        return mensa.getAddress();
    }

    @Override
    public List<Double> getCoordinates() {
        return mensa.getCoordinates();
    }
}
