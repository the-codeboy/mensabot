package ml.codeboy.thebot.commands.debug;

import ml.codeboy.thebot.Config;
import ml.codeboy.thebot.data.UserData;
import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.quotes.Person;
import ml.codeboy.thebot.quotes.Quote;
import ml.codeboy.thebot.quotes.QuoteManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListQuotes extends DebugCommand {
    public ListQuotes() {
        super("listQuotes", "", "lq");
    }

    @Override
    public void run(CommandEvent event) {
        event.replyError("Wäre vermutlich spam");
        /*
        Person[] persons = QuoteManager.getInstance().getPersons().toArray(new Person[0]);
        ArrayList<MessageEmbed> rep = new ArrayList<>();
        EmbedBuilder m = null;
        MessageEmbed[] msg;
        int i = 0;
        for (Person p : persons) {
            i=0;
            if (QuoteManager.getInstance().getQuotes(p.getName()).size() > 10)
                msg = new MessageEmbed[10];
            else
                msg = new MessageEmbed[QuoteManager.getInstance().getQuotes(p.getName()).size()];
            for (Quote q : QuoteManager.getInstance().getQuotes(p.getName())) {
                m = new EmbedBuilder();
                m.setTitle(p.getName());
                m.addField("Quote", q.getContent(), false);
                msg[i] = m.build();
                i++;
                if (i == 10) {
                    event.reply(msg);
                    i = 0;
                }
            }
            event.reply(msg);
        }*/
    }
}
