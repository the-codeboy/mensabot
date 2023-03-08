package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.events.MessageCommandEvent;
import ml.codeboy.thebot.util.Replyable;
import net.dv8tion.jda.api.EmbedBuilder;

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
    public void run(CommandEvent event) {
        String message;
        if (event.isMessageEvent()) {
            message = ((MessageCommandEvent) event).getContent();
        } else {
            //no slash command support for now
            return;
        }
        respondLatex(message, event);
    }
}
