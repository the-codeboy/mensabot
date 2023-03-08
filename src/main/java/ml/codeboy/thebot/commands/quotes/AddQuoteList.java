package ml.codeboy.thebot.commands.quotes;

import ml.codeboy.thebot.apis.mongoDB.DatabaseQuoteAPI;
import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.quotes.Person;
import ml.codeboy.thebot.quotes.Quote;
import ml.codeboy.thebot.quotes.QuoteManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddQuoteList extends Command {
    public AddQuoteList() {
        super("addquotelist", "adds a list of quotes to the bots database");
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData()
                .addOption(OptionType.STRING, "name", "the name of the person the quote is from", true, true)
                .addOption(OptionType.STRING, "content", "The quotes, separated by \"`\"", true);
    }

    @Override
    public void autoComplete(String option, List<String> options) {
        if (option.equals("name")) {
            for (Person person : QuoteManager.getInstance().getPersons()) {
                options.add(person.getName());
            }
        }
    }

    @Override
    public void run(CommandEvent event) {
        if (event.isSlashCommandEvent()) {
            String name = event.getSlashCommandEvent().getOption("name").getAsString();
            String content = event.getSlashCommandEvent().getOption("content").getAsString();

            addQuote(event, name, content);
        } else {
            String[] args = event.getArgs();
            if (args.length < 2)
                event.replyError("this needs at least two arguments");
            else {
                String name = args[0];
                ArrayList<String> a = new ArrayList<>(Arrays.asList(args));
                a.remove(0);
                String content = String.join(" ", a);
                addQuote(event, name, content);
            }
        }
    }

    private void addQuote(CommandEvent event, String name, String content) {
        EmbedBuilder reply = new EmbedBuilder().setTitle("Added quote").setColor(Color.GREEN);
        int i = 0;
        for (String s : content.split("`")) {
            DatabaseQuoteAPI.saveQuote(new Quote(s, System.currentTimeMillis(), name, event.getMember().getId()));
            i++;
        }
        reply.addField("Counter", "Added " + i + " quotes", false);
        event.reply(reply.build());
    }
}
