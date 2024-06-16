package com.the_codeboy.mensabot.commands;

import com.the_codeboy.mensabot.events.CommandEvent;
import com.the_codeboy.mensabot.util.Util;

public class GifCommand extends Command {
    public GifCommand() {
        super("gif", "sends a random gif");
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(Util.getRandomGif());
    }
}
