package ml.codeboy.thebot.commands.debug;

import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.events.CommandEvent;

public abstract class DebugCommand extends Command {
    public DebugCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
        setHidden(true);
    }

    @Override
    public void execute(CommandEvent event) {
        if (
                !event.getUser().getId().equals("412330776886247424")
                        && !event.getUser().getId().equals("902979780394221648")
                        && !event.getUser().getId().equals("358247499531681803")
        ) {
            event.replyError("Not a valid user");
            return;
        }
        super.execute(event);
    }
}
