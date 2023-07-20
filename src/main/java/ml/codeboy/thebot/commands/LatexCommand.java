package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.events.MessageCommandEvent;
import ml.codeboy.thebot.util.Replyable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;


public class LatexCommand extends Command {
    public LatexCommand() {
        super("latex", "turns latex into an image", "tex", "");
    }

    public static void respondLatex(String latex, Replyable replyable) {
        if (latex.length() > 200) {
            replyable.reply(new EmbedBuilder().setTitle("This is too much text").setDescription("the maximum is 200 characters :(").setColor(Color.RED).build());
        } else {
            String url = "https://latex.codecogs.com/png.image?\\inline&space;\\LARGE&space;\\dpi{200}\\color{white}";

            try {
                url += latex.replaceAll("\\s", "");
                ImageIO.read(new URL(url));// we only do that to insure the latex can be rendered

                replyable.reply(new MessageBuilder().append(url).build(), true);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(url);
                replyable.reply(true, new EmbedBuilder().setTitle("Normal method failed. Here is a picture rendered by google instead:")
                        .setImage("https://chart.apis.google.com/chart?cht=tx&chl=" + URLEncoder.encode(latex)).setColor(Color.RED).build());
            }
        }
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
