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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;


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
                double size = 1.1;
                url += latex.replaceAll("\\s", "");
                BufferedImage image = ImageIO.read(new URL(url));
                BufferedImage output = new BufferedImage((int) (image.getWidth() * size), (int) (image.getHeight() * size), BufferedImage.TYPE_INT_ARGB);
                output.getGraphics().drawImage(image, (int) ((size - 1) * image.getHeight() / 2),
                        (int) ((size - 1) * image.getHeight() / 2), null);

                File file = new File("images/" + new Random().nextInt() + ".png");
                file.getParentFile().mkdirs();
                try {
                    ImageIO.write(output, "png", file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                replyable.reply(new MessageBuilder().append("Here you go:").build(), true, file);
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(url);
                replyable.reply(new EmbedBuilder().setTitle("Normal method failed. Here is a picture rendered by google instead:")
                        .setImage("https://chart.apis.google.com/chart?cht=tx&chl=" + URLEncoder.encode(latex)).setColor(Color.RED).build());
            }
            //https://latex.codecogs.com/png.latex?

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
