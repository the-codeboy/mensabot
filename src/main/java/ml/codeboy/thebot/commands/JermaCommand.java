package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.events.CommandEvent;

public class JermaCommand extends Command {
    public JermaCommand() {
        super("jerma", "Sends a Jerma");
    }

    @Override
    public void run(CommandEvent event) {
        event.reply("https://static.wikia.nocookie.net/jerma-lore/images/e/e3/JermaSus.jpg/revision/latest?cb=20201206225609");
    }
}
