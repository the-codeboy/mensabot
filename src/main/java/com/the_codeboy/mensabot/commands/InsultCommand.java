package com.the_codeboy.mensabot.commands;

import com.the_codeboy.mensabot.apis.InsultApi;
import com.the_codeboy.mensabot.events.CommandEvent;

public class InsultCommand extends Command {

    public InsultCommand() {
        super("insult", "Insults you");
        setGuildOnlyCommand(false);
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(newBuilder().setTitle("Insult")
                .setDescription(InsultApi.getInstance().getObject()));
    }
}
