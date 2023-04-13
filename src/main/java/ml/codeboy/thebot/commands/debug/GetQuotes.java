package ml.codeboy.thebot.commands.debug;

import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.quotes.Quote;
import ml.codeboy.thebot.quotes.QuoteManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Echos all the quotes of the given person
 */
public class GetQuotes extends DebugCommand {
    public GetQuotes() {
        super("getQuotes", "", "gq");
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder m = null;
        MessageEmbed[] msg;
        int i = 0;
        if (QuoteManager.getInstance().getQuotes(event.getArgs()[0]).size() > 10)
            msg = new MessageEmbed[10];
        else
            msg = new MessageEmbed[QuoteManager.getInstance().getQuotes(event.getArgs()[0]).size()];
        for (Quote q : QuoteManager.getInstance().getQuotes(event.getArgs()[0])) {
            m = new EmbedBuilder();
            m.setTitle(event.getArgs()[0]);
            m.addField("Quote", q.getContent(), false);
            msg[i] = m.build();
            i++;
            if (i == 10) {
                event.reply(msg);
                i = 0;
            }
        }
        event.reply(msg);
    }
}
