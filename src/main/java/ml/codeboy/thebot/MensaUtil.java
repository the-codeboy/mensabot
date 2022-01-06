package ml.codeboy.thebot;

import com.github.codeboy.api.Meal;
import com.github.codeboy.api.Mensa;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Date;

public class MensaUtil {
    public static EmbedBuilder MealsToEmbed(Mensa mensa, Date date){
        EmbedBuilder builder=new EmbedBuilder();
        builder.setTitle("Meals in "+mensa.getName());
        builder.setDescription(dateToWord(date));

        for (Meal meal: mensa.getMeals(date)){
            builder.addField(meal.getName(),meal.getCategory()+
                    (meal.getPrices().getStudents()!=null?"\npreis: "+meal.getPrices().getStudents()+"€ ("+meal.getPrices().getOthers()+"€)":""),true);
        }

        return builder;
    }

    public static String dateToWord(Date date){
        long seconds=(date.getTime()-System.currentTimeMillis())/1000;
        if(Math.abs(seconds)< (60 * 60 * 12)){
            return "today";
        }
        if(seconds>0){
            if(seconds<3*60 * 60 * 12){
                return "tomorrow";
            }
            int days= (int) (1+(seconds-60 * 60 * 12)/(60*60*24));
            return "in "+days+" days";
        }
        if(seconds>-3*60 * 60 * 12){
            return "yesterday";
        }
        int days= (int) (1-(seconds+60 * 60 * 12)/(60*60*24));
        return days+" days ago";
    }

    public static Date wordToDate(String word){
        if(word.equalsIgnoreCase("today"))
            return new Date();
        if(word.equalsIgnoreCase("yesterday"))
            return new Date(System.currentTimeMillis()-1000*60*60*24);
        if(word.equalsIgnoreCase("tomorrow"))
            return new Date(System.currentTimeMillis()+1000*60*60*24);
        return null;
    }
}
