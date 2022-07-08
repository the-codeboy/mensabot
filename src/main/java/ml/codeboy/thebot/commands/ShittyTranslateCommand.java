package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.translator.GoogleTranslate;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.io.IOException;

public class ShittyTranslateCommand extends Command {
    public ShittyTranslateCommand() {
        super("shittyTranslate", "", "st");
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.STRING, "text", "the text to translate", true);
    }

    @Override
    public void run(CommandEvent event) {
        String text;
        if (event.isSlashCommandEvent()) {
            text = event.getSlashCommandEvent().getOption("text").getAsString();
        } else {
            text = String.join(" ", event.getArgs());
        }
        event.reply(event.getBuilder().setTitle("Please wait").setDescription("this might take a while").setImage("https://i.giphy.com/media/3o7bu3XilJ5BOiSGic/giphy.webp"));
        try {
            text = GoogleTranslate.shittyTranslate(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        event.edit(event.getBuilder().setTitle("Shitty translate").addField("", text, true));

    }
}
