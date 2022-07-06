package ml.codeboy.thebot.commands.image.meme;

import java.awt.*;

public class ChangeMyMindCommand extends MemeGeneratorCommand {
    public ChangeMyMindCommand() {
        super("https://i.kym-cdn.com/photos/images/original/001/663/358/bda.png", "ChangeMyMind", "", "cmm");
        setImageScale(0.25f);
        setBounds(textBounds);
    }

    private static final Rectangle textBounds = new Rectangle(750, 950, 900, 350);
}
