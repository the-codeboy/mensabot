package ml.codeboy.thebot.commands.quotes;

import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.quotes.Quote;
import ml.codeboy.thebot.quotes.QuoteManager;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class QuoteCommand extends Command {
    public QuoteCommand() {
        super("quote", "sends a quote");
//        setHidden(true);
        QuoteManager.getInstance();//this will cause the quotemanager to load all quotes
    }

    @Override
    public CommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.STRING, "author", "the name of the person the quote is from");
    }

    @Override
    public void run(CommandEvent event) {
        Quote quote;
        if (event.isSlashCommandEvent()) {
            SlashCommandEvent e = event.getSlashCommandEvent();
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
            if (args.length > 0) {
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