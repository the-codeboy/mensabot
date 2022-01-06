package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.Bot;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Collection;

public class Help extends Command {
    private final Bot bot;

    public Help(Bot bot) {
        super("help", "gives help");
        this.bot = bot;
    }

    @Override
    public void run(CommandEvent event) {
        Collection<Command> commands = bot.getCmdHandler().getCommands();
        EmbedBuilder embedBuilder = newBuilder();
        embedBuilder.setTitle("Help for commands", "https://youtu.be/watch?v=dQw4w9WgXcQ");
        for (Command command : commands) {
            embedBuilder.addField(command.getName(), command.getDescription(), true);
        }
        event.reply(embedBuilder);
    }
}
