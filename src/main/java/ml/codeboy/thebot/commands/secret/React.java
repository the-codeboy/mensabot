package ml.codeboy.thebot.commands.secret;

import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class React extends SecretCommand {
    public React() {
        super("react", "");
    }

    @Override
    public void run(CommandEvent event) {
        String[] args = event.getArgs();
        if (args.length > 0) {
            String channelId = args[0];
            String messageId = args[1];
            String reaction = args[2];
            JDA jda = event.getJdaEvent().getJDA();
            TextChannel channel = (TextChannel) jda.getGuildChannelById(channelId);
            Message message = channel.getHistoryAround(messageId, 1).complete().getMessageById(messageId);
            message.addReaction(jda.getEmojiById(reaction)).complete();
        }
    }
}
