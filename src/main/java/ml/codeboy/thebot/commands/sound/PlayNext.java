package ml.codeboy.thebot.commands.sound;

import ml.codeboy.thebot.events.CommandEvent;

public class PlayNext extends AudioCommand {
    public PlayNext() {
        super("playNext", "<song name>", "pn");
    }

    @Override
    public void run(CommandEvent event) {
        if (!ensureConnected(event))
            return;
        String link = String.join(" ", event.getArgs());
        queue(event, link, true);
    }
}
