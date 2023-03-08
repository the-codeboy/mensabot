package ml.codeboy.thebot.commands.image.meme;

import java.awt.*;

public class HotlineBlingCommand extends MemeGeneratorCommand {
    public HotlineBlingCommand() {
        super("https://i.imgflip.com/4/30b1gx.jpg", "HotlineBling", "", "hotline", "hb");
        setImageScale(2);
        setBounds(new Rectangle(130, 5, 100, 100), new Rectangle(130, 130, 100, 100));
    }
}
