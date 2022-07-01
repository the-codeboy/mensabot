package ml.codeboy.thebot.commands.debug;

import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.quotes.Person;
import ml.codeboy.thebot.quotes.Quote;
import ml.codeboy.thebot.quotes.QuoteManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;

public class getQuotes extends DebugCommand {
    public getQuotes() {
        super("getQuotes", "", "gq");
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder m = null;
        int i = 0;
        MessageEmbed[] msg = new MessageEmbed[10];
        for(Quote q : QuoteManager.getInstance().getQuotes(event.getArgs()[0])) {
            m = new EmbedBuilder();
            m.setTitle(event.getArgs()[0]);
            m.addField("Quote", q.getContent(),false);
            msg[i] = m.build();
            i++;
            if(i==10) {
                event.reply(msg);
                i=0;
            }
        }
    }
}
