package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.apis.AdviceApi;
import ml.codeboy.thebot.events.CommandEvent;

public class AdviceCommand extends Command {
    public AdviceCommand() {
        super("advice", "Gives you advice");
        setGuildOnlyCommand(false);
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(newBuilder().setTitle("Advice")
                .setDescription(AdviceApi.getInstance().getObject()));

    }
}
