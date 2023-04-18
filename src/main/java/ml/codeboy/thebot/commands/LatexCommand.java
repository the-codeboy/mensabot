package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.events.MessageCommandEvent;
import ml.codeboy.thebot.util.Replyable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.net.URLEncoder;

public class LatexCommand extends Command {
    public LatexCommand() {
        super("latex", "turns latex into an image", "tex", "");
    }

    public static void respondLatex(String latex, Replyable replyable) {
        if (latex.length() > 200) {
            replyable.reply(new EmbedBuilder().setTitle("This is too much text").setDescription("the maximum is 200 characters :(").setColor(Color.RED).build());
        } else
            replyable.reply("https://chart.apis.google.com/chart?cht=tx&chl=" + URLEncoder.encode(latex), true);
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.STRING, "latex", "the latex you want to render", true);
    }

    @Override
    public void run(CommandEvent event) {
        String message;
        if (event.isMessageEvent()) {
            message = ((MessageCommandEvent) event).getContent();
        } else {
            message = event.getSlashCommandEvent().getOption("latex").getAsString();
        }
        respondLatex(message, event);
    }
}
