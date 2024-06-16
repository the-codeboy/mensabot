package com.the_codeboy.mensabot.commands;

import com.the_codeboy.mensabot.apis.Meme;
import com.the_codeboy.mensabot.apis.MemeApi;
import com.the_codeboy.mensabot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

public class MemeCommand extends Command {

    public MemeCommand() {
        super("meme", "sends a meme");
        setGuildOnlyCommand(false);
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
