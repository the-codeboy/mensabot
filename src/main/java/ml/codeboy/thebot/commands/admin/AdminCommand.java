package ml.codeboy.thebot.commands.admin;

import ml.codeboy.thebot.Config;
import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.events.CommandEvent;

public abstract class AdminCommand  extends Command {
    public AdminCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
        setHidden(true);
    }

    @Override
    public void execute(CommandEvent event) {
        if (!Config.getInstance().isAdminAccount(event.getUser())) {
            return;
        }
        super.execute(event);
    }
}
