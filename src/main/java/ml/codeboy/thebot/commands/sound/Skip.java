package ml.codeboy.thebot.commands.sound;

import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.util.Util;

public class Skip extends AudioCommand{
    public Skip() {
        super("skip");
    }

    @Override
    public void run(CommandEvent event) {

        event.reply(Util.sign(event.getBuilder().setTitle("Song skipped"),event));
        PlayerManager.getInstance().getMusicManager(event.getGuild()).scheduler.nextTrack();
}
}
