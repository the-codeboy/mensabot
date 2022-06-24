package ml.codeboy.thebot.commands;

import com.github.codeboy.jokes4j.Jokes4J;
import com.github.codeboy.jokes4j.api.Joke;
import com.github.codeboy.jokes4j.api.JokeType;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

public class JokeCommand extends Command {
    public JokeCommand() {
        super("joke", "tells a joke");
    }

    @Override
    public void run(CommandEvent event) {
        Joke joke = null;

        joke = Jokes4J.getInstance().getJoke();

        EmbedBuilder builder = event.getBuilder();
        if (joke.getType() == JokeType.single) {
            builder.setDescription(joke.getJoke());
        } else if (joke.getType() == JokeType.twopart) {
            builder.setTitle(joke.getSetup()).setDescription(joke.getDelivery());
        }
        event.reply(builder);
    }
}
