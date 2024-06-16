package com.the_codeboy.mensabot.commands.sound;

import com.the_codeboy.mensabot.events.CommandEvent;

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
