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


    protected void drawString(Graphics g, String s, Rectangle position) {
        drawString(g, s, position, Color.BLACK);
    }

    protected void drawString(Graphics g, String s, Rectangle position, Color color) {
        g.setFont(g.getFont().deriveFont(1f));

        while (fits(g, s, position)) {
            changeSize(g, 1);
        }
        changeSize(g, -1);

        g.setColor(color);
        g.drawString(s, position.x, position.y + position.height);
    }

    private boolean fits(Graphics g, String msg, Rectangle pos) {
        return biggerThan(pos, g.getFontMetrics().getStringBounds(msg, g));
    }

    private boolean biggerThan(Rectangle2D r, Rectangle2D r2) {
        return r.getWidth() > r2.getWidth() && r.getHeight() > r2.getHeight();
    }

    private void changeSize(Graphics g, int amount) {
        g.setFont(g.getFont().deriveFont((float) g.getFont().getSize() + amount));
    }

    protected abstract void generateImage(CommandEvent event);
}
