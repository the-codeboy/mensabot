package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.apis.InsultApi;
import ml.codeboy.thebot.events.CommandEvent;

public class InsultCommand extends Command {

    public InsultCommand() {
        super("insult", "Insults you");
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(newBuilder().setTitle("Insult")
                .setDescription(InsultApi.getInstance().getObject()));
    }
}
