package com.the_codeboy.mensabot;

import com.github.codeboy.jokes4j.Jokes4J;
import com.github.codeboy.jokes4j.api.Flag;
import com.github.codeboy.jokes4j.api.JokeRequest;
import com.the_codeboy.mensabot.apis.AdviceApi;
import com.the_codeboy.mensabot.quotes.Quote;
import com.the_codeboy.mensabot.quotes.QuoteManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

import java.util.Timer;
import java.util.TimerTask;

public class StatusChanger {
    private final JDA jda;

    public StatusChanger(JDA jda) {
        this.jda = jda;
        changeStatus();
    }

    private void changeStatus() {
        new Timer().schedule(new TimerTask() {
            public void run() {
                String status;
                do {
                    status = getRandomStatus();
                } while (status.length() > 128 || status.length() == 0);
                jda.getPresence().setActivity(Activity.of(Activity.ActivityType.STREAMING, status,
                        "https://www.youtube.com/watch?v=dQw4w9WgXcQ&v=watch&feature=youtu.be"));
            }
        }, 0, 60_000);
    }

    private String getRandomStatus() {
        return getRandomJokeStatus();
    }

    private String getRandomJokeStatus(){
        return Jokes4J.getInstance()
                .getJoke(
                        new JokeRequest.Builder().blackList(Flag.explicit, Flag.nsfw, Flag.racist, Flag.sexist).build())
                .getJoke();
    }

    private String getRandomAdviceStatus() {
        return AdviceApi.getInstance().getObject();
    }

    private String getRandomQuoteStatus() {
        Quote quote;
        String status;
        quote = QuoteManager.getInstance().getRandomQuote();
        if (quote == null)
            return "";
        status = "\"" + quote.getContent() +
                "\"\n - " + quote.getPerson();
        return status;
    }
}
