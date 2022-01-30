package ml.codeboy.thebot.commands.sound;


import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.util.Util;

public class RemoveTrack extends AudioCommand {
    public RemoveTrack() {
        super("removeTrack", "<trackId>", "rmt");
    }

    @Override
    public void run(CommandEvent event) {
        String arg = event.getArgs()[0];
        if (Util.isInt(arg)) {
            boolean success = event.getManager().scheduler.removeTrack(Util.toInt(arg, -1));
            if (success)
                event.reply("success");
            else
                event.replyError("Could not remove song with id " + arg + " from queue");
        } else event.replyError("wrong usage");
    }
}
