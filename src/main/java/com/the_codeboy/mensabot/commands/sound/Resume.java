package com.the_codeboy.mensabot.commands.sound;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.the_codeboy.mensabot.events.CommandEvent;
import com.the_codeboy.mensabot.util.Util;

public class Resume extends AudioCommand {
    public Resume() {
        super("resume");
    }

    @Override
    public void run(CommandEvent event) {
        AudioPlayer player = PlayerManager.getInstance().getMusicManager(event.getGuild()).audioPlayer;
        player.setPaused(false);

        event.reply(Util.sign(event.getBuilder().setTitle("Song resumed"), event));
    }
}
