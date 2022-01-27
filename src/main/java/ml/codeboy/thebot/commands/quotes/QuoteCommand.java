package ml.codeboy.thebot.commands.quotes;

import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.quotes.Quote;
import ml.codeboy.thebot.quotes.QuoteManager;
import net.dv8tion.jda.api.EmbedBuilder;

public class QuoteCommand extends Command {
    public QuoteCommand() {
        super("quote", "sends a quote");
//        setHidden(true);
        QuoteManager.getInstance();//this will cause the quotemanager to load all quotes
    }

    @Override
    public void run(CommandEvent event) {
        Quote quote;
        String[]args=event.getArgs();
        if(args.length>0){
            String name=args[0];
            quote=QuoteManager.getInstance().getRandomQuote(name);
            if(quote==null){
                event.replyError("Unable to find quotes for "+name);
                return;
            }
        }else {
            quote = QuoteManager.getInstance().getRandomQuote();
        }
        EmbedBuilder builder=event.getBuilder();
        builder.setTitle(quote.getContent())
                .setDescription("||"+quote.getPerson()+"||");
        event.reply(builder);
    }
}
