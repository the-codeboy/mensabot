package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.apis.TrumpQuotesApi;
import ml.codeboy.thebot.events.CommandEvent;

public class TrumpQuoteCommand extends Command {

    public TrumpQuoteCommand() {
        super("trump", "sends trump quotes");
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(TrumpQuotesApi.getApi().getObject().createEmbed(newBuilder()));
    }
}
