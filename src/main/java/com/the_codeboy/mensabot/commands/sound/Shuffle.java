package com.the_codeboy.mensabot.commands.sound;

import com.the_codeboy.mensabot.events.CommandEvent;

public class Shuffle extends AudioCommand {
    public Shuffle() {
        super("shuffle");
    }

    @Override
    public void run(CommandEvent event) {
        shuffle(event);
        event.reply("success");
    }
}
