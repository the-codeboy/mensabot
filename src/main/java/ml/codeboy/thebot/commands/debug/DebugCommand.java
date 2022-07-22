package ml.codeboy.thebot.commands.debug;

import ml.codeboy.thebot.Config;
import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.events.CommandEvent;

public abstract class DebugCommand extends Command {
    public DebugCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
        setHidden(true);
    }

    @Override
    public void execute(CommandEvent event) {
        if (!Config.getInstance().isDebugAccount(event.getUser())) {
            return;
        }
        super.execute(event);
    }
}
