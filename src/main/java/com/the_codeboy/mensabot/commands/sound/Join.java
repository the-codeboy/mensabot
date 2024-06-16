package com.the_codeboy.mensabot.commands.sound;

import com.the_codeboy.mensabot.events.CommandEvent;
import com.the_codeboy.mensabot.util.Util;

public class Join extends AudioCommand {
    public Join() {
        super("join");
    }

    @Override
    public void run(CommandEvent event) {
        if (ensureConnected(event)) {
            event.reply(Util.sign(event.getBuilder().setTitle("Joined voicechannel successfully"), event));
        }
    }
}
