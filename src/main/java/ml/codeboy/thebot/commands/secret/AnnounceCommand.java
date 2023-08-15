package ml.codeboy.thebot.commands.secret;

import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;

public class AnnounceCommand extends SecretCommand {
    public AnnounceCommand() {
        super("announce", "");
    }

    @Override
    public void run(CommandEvent event) {
        if (event.getArgs().length == 0) {
            getCommandHandler().sendMealsToAllGuilds();
            event.reply("sent meals");
        } else {
            String content = String.join(" ", event.getArgs());
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Announcement")
                    .setDescription(content)
                    .setColor(0x00ff00);
            Message message = new MessageBuilder().setEmbeds(builder.build()).build();
            getCommandHandler().sendAnnouncementToAllGuilds(message);
            event.reply("sent Announcement");
        }
    }
}
