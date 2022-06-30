package ml.codeboy.thebot.commands.sound;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.util.Util;

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
