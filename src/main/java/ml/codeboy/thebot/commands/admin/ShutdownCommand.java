package ml.codeboy.thebot.commands.admin;

import ml.codeboy.thebot.events.CommandEvent;

public class ShutdownCommand extends AdminCommand{
    public ShutdownCommand() {
        super("shutdown",
                "Shuts the bot down. Ideally it should start again after this","restart");
    }

    @Override
    public void run(CommandEvent event) {
        event.replyError("Stopping bot");
        System.exit(0);// probably should add something to make sure all connections are closed before running this
    }
}
