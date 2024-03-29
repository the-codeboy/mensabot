package ml.codeboy.thebot.commands.sound;

import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.util.Util;

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
