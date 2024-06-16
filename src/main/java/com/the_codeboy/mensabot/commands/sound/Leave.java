package com.the_codeboy.mensabot.commands.sound;

import com.the_codeboy.mensabot.events.CommandEvent;
import com.the_codeboy.mensabot.util.Util;

public class Leave extends AudioCommand {
    public Leave() {
        super("leave");
    }

    @Override
    public void run(CommandEvent event) {
        PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer.stopTrack();
        if (ensureSameChannel(event)) {
            event.reply(Util.sign(event.getBuilder().setTitle("Left voicechannel successfully"), event));
            event.getGuild().getAudioManager().closeAudioConnection();
        }
    }
}
