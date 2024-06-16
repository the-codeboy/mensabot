package com.the_codeboy.mensabot.commands.sound;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.the_codeboy.mensabot.events.CommandEvent;

import static com.the_codeboy.mensabot.util.Util.sendTrackInfo;

public class CurrentTrack extends AudioCommand {

    public CurrentTrack() {
        super("current");
    }

    @Override
    public void run(CommandEvent event) {

        if (!ensureConnected(event))
            return;

        AudioTrack current = event.getManager().audioPlayer.getPlayingTrack();

        if (current != null) {
            sendTrackInfo(event, current);
            event.getManager().scheduler.setLatestEvent(event);
        } else {
            event.replyError("There is no song currently playing!");
        }

    }

}
