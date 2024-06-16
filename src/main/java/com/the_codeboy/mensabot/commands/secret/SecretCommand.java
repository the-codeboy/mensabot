package com.the_codeboy.mensabot.commands.secret;

import com.the_codeboy.mensabot.Config;
import com.the_codeboy.mensabot.commands.Command;
import com.the_codeboy.mensabot.events.CommandEvent;

public abstract class SecretCommand extends Command {
    public SecretCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
        setHidden(true);
    }

    @Override
    public void execute(CommandEvent event) {
        if (!Config.getInstance().admins.contains(event.getUser().getId()))
            return;
        super.execute(event);
    }
}
