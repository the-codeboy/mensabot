package ml.codeboy.thebot.commands.sound;

import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.util.Util;

public class Join extends AudioCommand {
    public Join() {
        super("join");
    }

    @Override
    public void run(CommandEvent event) {
        if(ensureConnected(event)) {
            event.reply(Util.sign(event.getBuilder().setTitle("Joined voicechannel successfully"), event));
            return;
        }
    }
}
