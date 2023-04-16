package ml.codeboy.thebot.commands.quotes;

import ml.codeboy.thebot.apis.mongoDB.DatabaseQuoteAPI;
import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.quotes.Person;
import ml.codeboy.thebot.quotes.Quote;
import ml.codeboy.thebot.quotes.QuoteManager;
import ml.codeboy.thebot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AddQuote extends Command {
    public AddQuote() {
        super("addquote", "adds a quote to the bots database");
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData()
                .addOption(OptionType.STRING, "name", "the name of the person the quote is from", true, true)
                .addOption(OptionType.STRING, "content", "the content of the quote", true);
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
        Quote quote = new Quote(content, System.currentTimeMillis(), name, event.getMember().getId());
        getLogger().info(quote.getPerson() + ": " + quote.getContent());

        int length = content.length();

        String lowContent = content.toLowerCase();

        ArrayList<MessageEmbed> embeds = new ArrayList<>();
        embeds.add(new EmbedBuilder().setTitle("Added quote").setColor(Color.GREEN).build());
        embeds.add(new EmbedBuilder().addField(" ", content + "\n||" + name + "||", false).build());

        for (Quote q : QuoteManager.getInstance().getQuotes(name)) {
            int distance = Util.calculateDistance(lowContent, q.getContent().toLowerCase());
            int quoteLength = q.getContent().length();
            getLogger().info(distance + ": " + q.getContent());
            int lengthDifference = Math.abs(length - quoteLength);
            double maxLength = Math.max(length, quoteLength);
            double chance = 1 - distance / maxLength - lengthDifference / maxLength;
            getLogger().info("chance: " + chance);

            if (chance == 1) {
                event.reply(new EmbedBuilder().setTitle("I already have this quote :(")
                        .setDescription("But feel free to add any other quote you might have!")
                        .setColor(Color.RED));
                return;// quote will not get added
            } else if (chance > 0.3) {
                boolean slash = event.isSlashCommandEvent();
                String id = "delete-quote:" + UUID.randomUUID();
                Button buttonDelete = Button.danger(id, "delete quote");
                MessageEmbed embed = new EmbedBuilder().setTitle("I have the feeling I already know a similar quote")
                        .setDescription("Please click the button below if they are the same quote - otherwise you can ignore this")
                        .addField(" ", q.getContent() + "\n||" + q.getPerson() + "||", false)
                        .setColor(Color.RED).build();

                getInteractionHandler().registerButtonListener(id, e ->
                        {
                            e.deferReply(true).queue();
                            e.getHook().sendMessageEmbeds(new EmbedBuilder().setColor(Color.RED)
                                    .setTitle("Quote removed")
                                    .build()).setEphemeral(true).queue();
                            DatabaseQuoteAPI.removeQuote(quote);
                            getLogger().info("duplicate quote removed by " + e.getUser().getName());
                            return true;
                        }
                );

                if (slash) {
                    event.getSlashCommandEvent().getInteraction().getHook()
                            .sendMessageEmbeds(embed).addActionRow(buttonDelete).setEphemeral(true).queue();
                } else {
                    event.getChannel()
                            .sendMessageEmbeds(embed).setActionRow(buttonDelete)
                            .reference(event.getMessageReceivedEvent().getMessage()).queue();
                }
                DatabaseQuoteAPI.saveQuote(quote);
                return;//prevent default behavior
            }
        }
        DatabaseQuoteAPI.saveQuote(quote);
        event.reply(embeds.toArray(new MessageEmbed[0]));// todo make another method for this
    }
}
