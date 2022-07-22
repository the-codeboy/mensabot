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
        setGuildOnlyCommand(false);
    }

    @Override
    public void run(CommandEvent event) {
        Collection<Command> commands = bot.getCmdHandler().getCommands();
        EmbedBuilder embedBuilder = newBuilder();
        embedBuilder.setTitle("Help for commands", "https://cntr.click/g3PZ5hm");
        for (Command command : commands) {
            if (!command.isHidden())
                embedBuilder.addField(command.getName(), command.getDescription(), true);
        }
        event.reply(embedBuilder);
    }
}
