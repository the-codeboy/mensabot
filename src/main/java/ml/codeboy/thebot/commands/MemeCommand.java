package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.apis.Meme;
import ml.codeboy.thebot.apis.MemeApi;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

public class MemeCommand extends Command {

    public MemeCommand() {
        super("meme", "sends a meme");
    }

    @Override
    public void run(CommandEvent event) {
        Meme meme = MemeApi.getInstance().getObject();
        EmbedBuilder builder = event.getBuilder();
        builder.setTitle(meme.getTitle(), meme.getPostLink())
                .setAuthor(meme.getAuthor())
                .setImage(meme.getUrl())
                .setFooter(meme.getUps() + " upvotes");
        event.reply(builder);
    }
}
