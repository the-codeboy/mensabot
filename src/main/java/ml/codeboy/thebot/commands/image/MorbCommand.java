package ml.codeboy.thebot.commands.image;

import de.cerus.jgif.GifImage;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.util.Util;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MorbCommand extends ImageCommand {
    private final Random random = new Random();

    public MorbCommand() {
        super("morb", "I'm gonna morb");
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

        GifImage image = new GifImage();
        image.setOutputFile(new File("images/morb_" + random.nextInt() + ".gif"));
        try {
            image.loadFrom(new File("images/morb.gif"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < image.getFrames().size(); i++) {
            BufferedImage frame = image.getFrame(i);
            Graphics graphics = frame.getGraphics();
            graphics.drawImage(img, 80, 50, 64, 64, null);
            graphics.dispose();
            image.setFrame(i, frame);
        }
        image.save();
        event.getChannel().sendFile(image.getOutputFile()).complete();
        image.getOutputFile().delete();
    }

}
