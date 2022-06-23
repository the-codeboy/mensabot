package ml.codeboy.thebot.apis;

import com.github.codeboy.Util;
import com.github.codeboy.api.Meal;
import com.github.codeboy.api.Mensa;

import java.util.Date;
import java.util.List;

public class RWTHMensa implements Mensa {
    private final Mensa mensa;

    public RWTHMensa(Mensa mensa) {
        this.mensa = mensa;
    }

    @Override
    public List<Meal> getMeals() {
        return getMeals(new Date());
    }

    @Override
    public List<Meal> getMeals(Date date) {
        return getMeals(Util.dateToString(date));
    }

    @Override
    public List<Meal> getMeals(String s) {
        List<Meal> meals = mensa.getMeals(s);
        for (int i = 0; i < meals.size(); i++) {
            Meal meal = meals.get(i);
            if (meal.getCategory().equals("Hauptbeilagen") || meal.getCategory().equals("Nebenbeilage")) {
                String[] submeals = meal.getName().split(" oder ");
                if (submeals.length > 1 && !"".equals(submeals[1])) {
                    meals.remove(i);
                    i--;
                    for (String name : submeals) {
                        Meal submeal = new Meal(name, meal.getCategory(), meal.getNotes(), meal.getPrices());
                        meals.add(submeal);
                    }
                }
            }
        }
        return meals;
    }

    @Override
    public boolean isOpen() {
        return isOpen(new Date());
    }

    @Override
    public boolean isOpen(Date date) {
        return isOpen(Util.dateToString(date));
    }

    @Override
    public boolean isOpen(String s) {
        return mensa.isOpen(s);
    }

    @Override
    public int getId() {
        return mensa.getId();
    }

    @Override
    public String getName() {
        return mensa.getName();
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
