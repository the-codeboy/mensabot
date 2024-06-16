package com.the_codeboy.mensabot.commands.image.meme;

import java.awt.*;

public class ChangeMyMindCommand extends MemeGeneratorCommand {
    private static final Rectangle textBounds = new Rectangle(750, 950, 900, 350);

    public ChangeMyMindCommand() {
        super("https://i.kym-cdn.com/photos/images/original/001/663/358/bda.png", "ChangeMyMind", "", "cmm");
        setImageScale(0.25f);
        setBounds(textBounds);
    }
}
