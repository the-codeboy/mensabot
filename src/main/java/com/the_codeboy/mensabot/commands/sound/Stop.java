package com.the_codeboy.mensabot.commands.sound;


import com.the_codeboy.mensabot.events.CommandEvent;
import com.the_codeboy.mensabot.util.Util;

public class Stop extends AudioCommand {
    public Stop() {
        super("stop");
    }

    @Override
    public void run(CommandEvent event) {
        PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer.stopTrack();

        event.reply(Util.sign(event.getBuilder().setTitle("Song stopped"), event));
    }
}
