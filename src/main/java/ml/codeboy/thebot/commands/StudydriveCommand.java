package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.net.URLEncoder;

public class StudydriveCommand extends Command {
    public StudydriveCommand() {
        super("studydrive", "Allows you to download pdfs from studydrive", "study");
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.STRING, "url", "The url of the studydrive document", true);
    }

    @Override
    public void run(CommandEvent event) {
        String[] args = event.getArgs();
        if (args.length == 0) {
            event.replyError("Please provide a link to a studydrive document (works only for pdfs)");
            return;
        }
        String url = args[0];
        url = "https://study.the-codeboy.com/download/" + URLEncoder.encode(url);
        event.reply(event.getBuilder().setTitle("Download PDF")
                .setDescription("You can download the pdf [here](" + url + ")"));
    }
}
