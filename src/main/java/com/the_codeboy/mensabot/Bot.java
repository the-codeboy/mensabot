package com.the_codeboy.mensabot;

import com.the_codeboy.mensabot.listeners.CommandHandler;
import net.dv8tion.jda.api.JDA;

public interface Bot {
    JDA getJda();

    CommandHandler getCmdHandler();
}
