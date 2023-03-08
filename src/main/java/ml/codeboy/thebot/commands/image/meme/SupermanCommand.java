package ml.codeboy.thebot.commands.image.meme;

import java.awt.*;
import java.io.File;

public class SupermanCommand extends MemeGeneratorCommand {
    public SupermanCommand() {
        super("file://" + new File("images/superman.png").getAbsolutePath(), "superman", "");
        setBounds(new Rectangle(450, 140, 220, 60));
    }
}
