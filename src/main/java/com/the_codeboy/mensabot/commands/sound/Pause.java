package com.the_codeboy.mensabot.commands.sound;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.the_codeboy.mensabot.events.CommandEvent;
import com.the_codeboy.mensabot.util.Util;

public class Pause extends AudioCommand {
    public Pause() {
        super("pause");
    }

    @Override
    public void run(CommandEvent event) {
        AudioPlayer player = PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer;
        player.setPaused(!player.isPaused());

        event.reply(Util.sign(event.getBuilder().setTitle("Song paused"), event));
    }
}
