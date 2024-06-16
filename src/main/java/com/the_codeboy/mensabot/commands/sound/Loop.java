package com.the_codeboy.mensabot.commands.sound;

import com.the_codeboy.mensabot.events.CommandEvent;
import com.the_codeboy.mensabot.util.Util;

public class Loop extends AudioCommand {
    public Loop() {
        super("loop", "allows you to loop a song");
    }

    @Override
    public void run(CommandEvent event) {
        if (!ensureConnected(event))
            return;
        String firstArg = event.getArgs().length > 0 ? event.getArgs()[0] : "";

        switch (firstArg) {
            case "": {
                event.getManager().scheduler.loop(Integer.MAX_VALUE);
                event.reply("Songs will be repeated until you skip them");
                break;
            }
            case "none": {
                event.getManager().scheduler.loop(0);
                event.getManager().scheduler.dontLoopQueue();
                event.reply("Songs and the queue won't be repeated");
                break;
            }
            case "queue": {
                event.getManager().scheduler.loopQueue();
                event.reply("The current queue will be repeated once the queue ends");
                break;
            }
            default: {
                if (Util.isInt(firstArg)) {
                    int loop = Util.toInt(firstArg, 0);
                    event.getManager().scheduler.loop(loop);
                    event.reply("Songs will be repeated " + loop + " times");
                } else {
                    event.reply("wrong usage");
                }
            }
        }
    }
}
