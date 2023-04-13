package ml.codeboy.thebot.commands.image;

import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.util.Util;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ShitCommand extends ImageCommand {
    public ShitCommand() {
        super("shit", "");
    }

    @Override
    protected void generateImage(CommandEvent event) {

        BufferedImage img = null;
        User user = event.getUser();
        if (event.isMessageEvent()) {
            List<Member> members = event.getMessageReceivedEvent().getMessage().getMentions().getMembers();
            if (!members.isEmpty()) {
                user = members.get(0).getUser();
            }
        }
        try {
            img = Util.getAvatarImage(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedImage shit = null;
        try {
            shit = ImageIO.read(new URL("https://i.pinimg.com/originals/00/0b/26/000b26a172951b7be6a6107cb8c775dc.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Graphics g = shit.createGraphics();
        g.drawImage(img, 300, 800, 128, 128, null);
        g.dispose();

        event.reply(shit, "jpg", "shit");
    }
}
