package com.the_codeboy.mensabot.commands.sound;

import com.the_codeboy.mensabot.events.CommandEvent;

public class Queue extends AudioCommand {
    public Queue() {
        super("queue", "", "play");
    }

    @Override
    public void run(CommandEvent event) {
        if (event.getArgs().length == 0) {
            event.getManager().scheduler.sendQueueInfo(event);
            return;
        }

        if (!ensureConnected(event))
            return;
        String link = String.join(" ", event.getArgs());
        queue(event, link);
    }
}
