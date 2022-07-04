package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.apis.ChuckNorrisJokesApi;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

public class AmogusCommand extends Command {
    public AmogusCommand() {
        super("amogus", "Sends an amogus", "sus", "amongus", "jermasus");
    }

    @Override
    public void run(CommandEvent event) {
        event.reply("https://static.wikia.nocookie.net/jerma-lore/images/e/e3/JermaSus.jpg/revision/latest?cb=20201206225609");
    }
}
