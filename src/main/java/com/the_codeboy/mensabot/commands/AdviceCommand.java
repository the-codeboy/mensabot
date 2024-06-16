package com.the_codeboy.mensabot.commands;

import com.the_codeboy.mensabot.apis.AdviceApi;
import com.the_codeboy.mensabot.events.CommandEvent;

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
