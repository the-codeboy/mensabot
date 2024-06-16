package com.the_codeboy.mensabot.commands;

import com.the_codeboy.mensabot.apis.ChuckNorrisJokesApi;
import com.the_codeboy.mensabot.events.CommandEvent;

public class ChuckNorrisJokeCommand extends Command {
    public ChuckNorrisJokeCommand() {
        super("chuckNorrisJoke", "Sends a Chuck Norris joke");
        setGuildOnlyCommand(false);
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(newBuilder().setTitle("Chuck Norris Joke")
                .setDescription(ChuckNorrisJokesApi.getInstance().getObject()));
    }
}
