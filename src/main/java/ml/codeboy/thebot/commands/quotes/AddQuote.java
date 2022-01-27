package ml.codeboy.thebot.commands.quotes;

import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.quotes.Person;
import ml.codeboy.thebot.quotes.Quote;
import ml.codeboy.thebot.quotes.QuoteManager;

import java.util.ArrayList;
import java.util.Arrays;

public class AddQuote extends Command {
    public AddQuote() {
        super("addquote", "adds a quote to the bots database");
        setHidden(true);
    }

    @Override
    public void run(CommandEvent event) {
        String[] args=event.getArgs();
        if(args.length<2)
            event.replyError("this needs at least two arguments");
        else {
            String name=args[0];
            ArrayList<String>a=new ArrayList<>(Arrays.asList(args));
            a.remove(0);
            String content=String.join(" ",a);
            Person person=QuoteManager.getInstance().getOrCreate(name);
            Quote quote=new Quote();
            quote.setTime(System.currentTimeMillis());
            quote.setContent(content);
            person.getQuotes().add(quote);
            person.save();
            event.reply("success");
        }
    }
}
