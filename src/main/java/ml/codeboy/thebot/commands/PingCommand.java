package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.events.SlashCommandCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping", "Measures the bots ping", "pong");
    }

    @Override
    public void run(CommandEvent event) {
        long startTime = System.nanoTime();
        if (event instanceof SlashCommandCommandEvent) {
            SlashCommandInteractionEvent event2 = event.getSlashCommandEvent();
            event2.getHook().sendMessage("loading").complete();
        } else {
            event.reply("loading");
        }
        long ping = System.nanoTime() - startTime;
        ping /= 2;
        ping /= 1000000;
        event.edit("Der Ping beträgt " + ping + " Millisekunden");
    }
}
