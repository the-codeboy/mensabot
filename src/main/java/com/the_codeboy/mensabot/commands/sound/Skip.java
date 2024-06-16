package com.the_codeboy.mensabot.commands.sound;

import com.the_codeboy.mensabot.events.CommandEvent;
import com.the_codeboy.mensabot.util.Util;

public class Skip extends AudioCommand {
    public Skip() {
        super("skip");
    }

    @Override
    public void run(CommandEvent event) {

        event.reply(Util.sign(event.getBuilder().setTitle("Song skipped"), event));
        PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.nextTrack();
    }
}
