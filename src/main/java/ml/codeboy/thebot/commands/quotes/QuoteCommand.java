package ml.codeboy.thebot.commands.quotes;

import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.quotes.Person;
import ml.codeboy.thebot.quotes.Quote;
import ml.codeboy.thebot.quotes.QuoteManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public class QuoteCommand extends Command {
    public QuoteCommand() {
        super("quote", "sends a quote");
//        setHidden(true);
        QuoteManager.getInstance();//this will cause the quotemanager to load all quotes
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.STRING, "author", "the name of the person the quote is from", false, true);
    }


    @Override
    public void autoComplete(String option, List<String> options) {
        if (option.equals("author")) {
            for (Person person : QuoteManager.getInstance().getPersons()) {
                options.add(person.getName());
            }
        }
    }

    @Override
    public void run(CommandEvent event) {
        Quote quote;
        if (event.isSlashCommandEvent()) {
            SlashCommandInteractionEvent e = event.getSlashCommandEvent();
            OptionMapping option = e.getOption("author");
            if (option != null) {
                String name = option.getAsString();
                quote = QuoteManager.getInstance().getRandomQuote(name);
                if (quote == null) {
                    event.replyError("Unable to find quotes for " + name);
                    return;
                }
            } else {
                quote = QuoteManager.getInstance().getRandomQuote();
            }

        } else {
            String[] args = event.getArgs();
            if (args.length > 0 && args[0].length()!=0) {
                String name = args[0];
                quote = QuoteManager.getInstance().getRandomQuote(name);
                if (quote == null) {
                    event.replyError("Unable to find quotes for " + name);
                    return;
                }
            } else {
                quote = QuoteManager.getInstance().getRandomQuote();
            }
        }
        event.reply(quote.builder());
    }
}
