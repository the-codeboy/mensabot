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
        Person[] persons = QuoteManager.getInstance().getPersons().toArray(new Person[0]);
        ArrayList<MessageEmbed> rep = new ArrayList<>();
        EmbedBuilder m = null;
        int s = 0;
        for(Person p : persons)
        {
            m = new EmbedBuilder();
            m.setTitle(p.getName());
            for(Quote q : p.getQuotes().toArray(new Quote[0]))
            {
                if(s+q.getContent().length()>1024)
                {
                    s=0;
                    rep.add(m.build());
                    m = new EmbedBuilder();
                    m.setTitle(p.getName());
                }
                m.addField("",q.getContent(), false);
                s+=q.getContent().length();
            }
           rep.add(m.build());
        }
        event.reply(rep.toArray(new MessageEmbed[0]));
    }
}
