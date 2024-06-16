package com.the_codeboy.mensabot.commands.debug;

import com.the_codeboy.mensabot.Config;
import com.the_codeboy.mensabot.commands.Command;
import com.the_codeboy.mensabot.events.CommandEvent;

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
