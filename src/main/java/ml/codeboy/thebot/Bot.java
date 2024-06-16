package ml.codeboy.thebot;

import ml.codeboy.thebot.listeners.CommandHandler;
import net.dv8tion.jda.api.JDA;

public interface Bot {
    JDA getJda();

    CommandHandler getCmdHandler();
}
