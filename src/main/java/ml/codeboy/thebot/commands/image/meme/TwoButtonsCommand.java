package ml.codeboy.thebot.commands.image.meme;

import java.awt.*;

public class TwoButtonsCommand extends MemeGeneratorCommand {
    public TwoButtonsCommand() {
        super("https://i.imgflip.com/4/1g8my4.jpg", "TwoButtons", "", "tb");
        setImageScale(2);
        setBounds(new Rectangle(5, 15, 100, 30), new Rectangle(60, 10, 100, 30), new Rectangle(5, 180, 150, 40));
    }
}
