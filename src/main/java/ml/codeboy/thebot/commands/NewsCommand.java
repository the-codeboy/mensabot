package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.apis.NewsApi;
import ml.codeboy.thebot.events.CommandEvent;

public class NewsCommand extends Command {

    public NewsCommand() {
        super("news", "Sends news articles");
        setGuildOnlyCommand(false);
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(NewsApi.getInstance().getObject().createEmbed(newBuilder()));
    }
}
