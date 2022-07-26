package ml.codeboy.thebot.commands.image;

import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.events.CommandEvent;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class ImageCommand extends Command {
    public ImageCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
        setHidden(true);
    }

    @Override
    public void run(CommandEvent event) {
        generateImage(event);
    }


    public static boolean drawString(Graphics g, String s, Rectangle position) {
        return drawString(g, s, position, Color.BLACK);
    }

    public static boolean drawString(Graphics g, String s, Rectangle position, Color color) {
        g.setFont(g.getFont().deriveFont(1f));

        while (fits(g, s, position)) {
            changeSize(g, 1);
        }
        changeSize(g, -1);

        if (g.getFont().getSize() < 8)
            return false;//this is too small to read

        g.setColor(color);
        g.drawString(s, position.x, position.y + position.height);
        return true;
    }

    public static boolean fits(Graphics g, String msg, Rectangle pos) {
        return biggerThan(pos, g.getFontMetrics().getStringBounds(msg, g));
    }

    public static boolean biggerThan(Rectangle2D r, Rectangle2D r2) {
        return r.getWidth() > r2.getWidth() && r.getHeight() > r2.getHeight();
    }

    public static void changeSize(Graphics g, int amount) {
        g.setFont(g.getFont().deriveFont((float) g.getFont().getSize() + amount));
    }

    protected abstract void generateImage(CommandEvent event);
}
