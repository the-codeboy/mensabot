package ml.codeboy.thebot.commands.debug;

import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.events.CommandEvent;

/**
 * Echos all the quotes of the given person
 */
public class SaveUsers extends DebugCommand {
    public SaveUsers() {
        super("saveUsers", "");
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(UserDataManager.getInstance().moveDataToCloud());
    }
}
