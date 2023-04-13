package ml.codeboy.thebot;

import net.dv8tion.jda.api.JDA;

public interface Bot {
    JDA getJda();

    CommandHandler getCmdHandler();
}
