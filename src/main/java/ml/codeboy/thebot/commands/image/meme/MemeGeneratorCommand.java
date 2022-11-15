package ml.codeboy.thebot.commands.image.meme;

import ml.codeboy.thebot.commands.image.ImageCommand;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.util.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MemeGeneratorCommand extends ImageCommand {
    private String type = "jpg";
    private Rectangle[] bounds;

    private float imageScale = 1;
    private BufferedImage image = null;

    public MemeGeneratorCommand(String image, String name, String description, String... aliases) {
        super(name, description, aliases);
        try {
            this.image = Util.getImageFromUrl(image);
        } catch (IOException e) {
            getLogger().error("Failed to load image: ", e);
        }
    }

    protected void setImageScale(float imageScale) {
        this.imageScale = imageScale;
    }

    protected void setType(String type) {
        this.type = type;
    }

    protected void setBounds(Rectangle... bounds) {
        this.bounds = bounds;
        for (Rectangle r:bounds){
            r.x*=imageScale;
            r.y*=imageScale;
            r.width*=imageScale;
            r.height*=imageScale;
        }
    }

    @Override
    protected void generateImage(CommandEvent event) {
        String text = String.join(" ", event.getArgs());
        String[] captions = text.split(";");
        Image background = this.image.getScaledInstance((int) (this.image.getWidth() * imageScale), (int) (this.image.getHeight() * imageScale), Image.SCALE_DEFAULT);
        BufferedImage image = new BufferedImage(background.getWidth(null), background.getHeight(null), BufferedImage.TYPE_INT_RGB);

        Graphics g = image.createGraphics();
        g.drawImage(background,0,0,null);
        for (int i = 0; i < bounds.length; i++) {
            if (captions.length <= i)
                break;
            boolean success = drawString(g, captions[i], bounds[i]);
            if (!success) {
                g.dispose();
                event.replyError("That is too much text!");
                return;
            }
        }
        g.dispose();

        event.reply(image, type, getName());
    }
}
