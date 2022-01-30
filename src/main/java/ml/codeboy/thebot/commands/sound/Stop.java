package ml.codeboy.thebot.commands.sound;


import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.util.Util;

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
