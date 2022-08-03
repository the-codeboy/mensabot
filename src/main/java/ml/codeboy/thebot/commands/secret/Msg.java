package ml.codeboy.thebot.commands.secret;

import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class Msg extends SecretCommand {
    public Msg() {
        super("msg", "");
    }

    @Override
    public void run(CommandEvent event) {
        String id = event.getArgs()[0];
        MessageChannel channel = (TextChannel) event.getJdaEvent().getJDA().getGuildChannelById(id);
        if (channel == null) {
            User user = event.getJdaEvent().getJDA().retrieveUserById(id).complete();
            if(user==null) {
                event.reply(":(");
                return;
            }
            channel=user.openPrivateChannel().complete();
        }
        String message = String.join(" ", event.getArgs());
        message = message.substring(event.getArgs()[0].length() + 1);
        channel.sendMessage(message).complete();
        event.getMessageReceivedEvent().getMessage().addReaction(Emoji.fromFormatted("✅")).queue();
    }
}
