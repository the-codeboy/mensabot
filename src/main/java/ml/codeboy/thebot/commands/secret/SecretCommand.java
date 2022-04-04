package ml.codeboy.thebot.commands.secret;

import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.events.CommandEvent;

public abstract class SecretCommand extends Command {
    public SecretCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
        setHidden(true);
    }

    @Override
    public void execute(CommandEvent event) {
        if (!event.getUser().getId().equals("412330776886247424"))
            return;
        super.execute(event);
    }
}
