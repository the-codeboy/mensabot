package ml.codeboy.thebot.commands.sound;

import ml.codeboy.thebot.events.CommandEvent;

public class Shuffle extends AudioCommand{
    public Shuffle() {
        super("shuffle");
    }

    @Override
    public void run(CommandEvent event) {
        shuffle(event);
        event.reply("success");
    }
}
