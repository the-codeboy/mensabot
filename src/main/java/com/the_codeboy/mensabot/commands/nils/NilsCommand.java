package com.the_codeboy.mensabot.commands.nils;

import com.the_codeboy.mensabot.commands.Command;
import com.the_codeboy.mensabot.events.CommandEvent;

public abstract class NilsCommand extends Command {
    public NilsCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
        setHidden(true);
    }

    @Override
    public void execute(CommandEvent event) {
        if (!event.getUser().getId().equals("358247499531681803") && !event.getUser().getId().equals("902979780394221648"))
            return;
        super.execute(event);
    }
}
