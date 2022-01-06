package ml.codeboy.thebot;

import net.dv8tion.jda.api.JDA;

public interface Bot {
    public JDA getJda();

    CommandHandler getCmdHandler();
}
