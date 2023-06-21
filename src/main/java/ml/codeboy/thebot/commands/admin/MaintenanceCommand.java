package ml.codeboy.thebot.commands.admin;

import ml.codeboy.thebot.Config;
import ml.codeboy.thebot.events.CommandEvent;

import java.io.IOException;

public class MaintenanceCommand extends AdminCommand {
    public MaintenanceCommand() {
        super("maintenance", "toggles maintenance mode");
    }

    @Override
    public void run(CommandEvent event) {
        Config config = Config.getInstance();
        config.maintenance = !config.maintenance;
        try {
            config.save();
        } catch (IOException e) {
            event.replyError("Failed to save config");
        }
        event.reply("Bot is now " + (config.maintenance ? "" : "no longer ") + "in maintenance mode");
    }
}
