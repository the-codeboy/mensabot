package ml.codeboy.thebot.commands.secret;

import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.entities.TextChannel;

public class Msg extends SecretCommand {
    public Msg() {
        super("msg", "");
    }

    @Override
    public void run(CommandEvent event) {
        TextChannel channel = (TextChannel) event.getJdaEvent().getJDA().getGuildChannelById(event.getArgs()[0]);
        String message = String.join(" ", event.getArgs());
        message = message.substring(event.getArgs()[0].length() + 1);
        channel.sendMessage(message).complete();
        event.getMessageReceivedEvent().getMessage().addReaction("✅").queue();
    }
}
