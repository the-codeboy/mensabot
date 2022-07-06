package ml.codeboy.thebot.commands.image.meme;

import java.awt.*;

public class Draw25Command extends MemeGeneratorCommand{
    public Draw25Command() {
        super("https://i.imgflip.com/4/3lmzyx.jpg", "Draw25", "", "25","draw");
        setImageScale(2);
        setBounds(new Rectangle(25,80,90,40),new Rectangle(135,5,110,30));
    }
}
