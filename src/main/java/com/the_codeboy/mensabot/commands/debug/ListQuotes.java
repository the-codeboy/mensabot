package com.the_codeboy.mensabot.commands.debug;

import com.the_codeboy.mensabot.events.CommandEvent;
import com.the_codeboy.mensabot.quotes.Person;
import com.the_codeboy.mensabot.quotes.Quote;
import com.the_codeboy.mensabot.quotes.QuoteManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;

public class ListQuotes extends DebugCommand {
    public ListQuotes() {
        super("listQuotes", "", "lq");
    }

    @Override
    public void run(CommandEvent event) {
        Person[] persons = QuoteManager.getInstance().getPersons().toArray(new Person[0]);
        ArrayList<MessageEmbed> rep = new ArrayList<>();
        EmbedBuilder m = null;
        int s = 0;
        for (Person p : persons) {
            m = new EmbedBuilder();
            m.setTitle(p.getName());
            for (Quote q : p.getQuotes()) {
                if (s + q.getContent().length() > 1024) {
                    s = 0;
                    rep.add(m.build());
                    if (rep.size() >= 10) {
                        event.reply(rep.toArray(new MessageEmbed[0]));
                        rep.clear();
                    }
                    m = new EmbedBuilder();
                    m.setTitle(p.getName());
                }
                m.addField("", q.getContent(), false);
                s += q.getContent().length();
            }
            rep.add(m.build());
            if (rep.size() >= 10) {
                event.reply(rep.toArray(new MessageEmbed[0]));
                rep.clear();
            }
        }
        if (rep.size() > 0)
            event.reply(rep.toArray(new MessageEmbed[0]));
    }
}
