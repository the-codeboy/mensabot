package ml.codeboy.thebot.apis;

import com.github.codeboy.Util;
import com.github.codeboy.api.Meal;
import com.github.codeboy.api.Mensa;
import com.github.codeboy.api.MensaImpl;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RWTHMensa implements Mensa {
    private Mensa mensa;

    public RWTHMensa(Mensa mensa) {
        this.mensa = mensa;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                setMensa(new MensaImpl(mensa.getId(), mensa.getName(), mensa.getCity(), mensa.getAddress(), mensa.getCoordinates()));
            }
        }, 1000 * 60 * 60, 1000 * 60 * 60);//clears the mensas cache once per hour
    }

    private void setMensa(Mensa mensa) {
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
