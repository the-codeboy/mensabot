package com.the_codeboy.mensabot.commands;

import com.the_codeboy.mensabot.apis.TrumpQuotesApi;
import com.the_codeboy.mensabot.events.CommandEvent;

public class TrumpQuoteCommand extends Command {

    public TrumpQuoteCommand() {
        super("trump", "sends trump quotes");
        setGuildOnlyCommand(false);
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(TrumpQuotesApi.getApi().getObject().createEmbed(newBuilder()));
    }
}
