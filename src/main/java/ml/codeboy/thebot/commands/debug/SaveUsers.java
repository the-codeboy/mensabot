package ml.codeboy.thebot.commands.debug;

import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.quotes.Quote;
import ml.codeboy.thebot.quotes.QuoteManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

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
