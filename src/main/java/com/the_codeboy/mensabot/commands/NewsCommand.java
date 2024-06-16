package com.the_codeboy.mensabot.commands;

import com.the_codeboy.mensabot.apis.NewsApi;
import com.the_codeboy.mensabot.events.CommandEvent;

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
