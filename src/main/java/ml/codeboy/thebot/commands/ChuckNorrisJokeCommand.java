package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.apis.ChuckNorrisJokesApi;
import ml.codeboy.thebot.events.CommandEvent;

public class ChuckNorrisJokeCommand extends Command {
    public ChuckNorrisJokeCommand() {
        super("chuckNorrisJoke", "Sends a Chuck Norris joke");
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(newBuilder().setTitle("Chuck Norris Joke")
                .setDescription(ChuckNorrisJokesApi.getInstance().getObject()));
    }
}
