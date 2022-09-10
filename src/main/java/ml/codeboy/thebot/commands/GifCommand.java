package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.util.Util;

public class GifCommand extends Command {
    public GifCommand() {
        super("gif", "sends a random gif");
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(Util.getRandomGif());
    }
}
