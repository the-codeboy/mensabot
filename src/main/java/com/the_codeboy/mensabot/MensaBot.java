package com.the_codeboy.mensabot;

import com.the_codeboy.mensabot.data.UserDataManager;
import com.the_codeboy.mensabot.listeners.CommandHandler;
import com.the_codeboy.mensabot.listeners.InteractionHandler;
import com.the_codeboy.mensabot.listeners.PrivateListener;
import com.the_codeboy.mensabot.listeners.UselessListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class MensaBot implements Bot {

    private static final MensaBot instance = new MensaBot();
    private final CommandHandler commandHandler;
    private final JDA jda;
    private final Logger logger
            = LoggerFactory.getLogger(getClass());

    public MensaBot() {
        try {
            jda = JDABuilder.createDefault(Config.getInstance().token)
                    .enableIntents(GatewayIntent.DIRECT_MESSAGES,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_PRESENCES)
                    .enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY).build();
            jda.awaitReady();
        } catch (InterruptedException | LoginException e) {
            throw new RuntimeException(e);
        }
        commandHandler = new CommandHandler(this);
        jda.addEventListener(commandHandler);
        jda.addEventListener(InteractionHandler.getInstance());
        jda.addEventListener(new UselessListener(this));
        jda.addEventListener(new PrivateListener());
        AnnouncementTracker.registerAnnouncementTracker(jda);
        logger.info("Bot started");
    }

    public static void main(String[] args) {
        UserDataManager.getInstance();// to load userdata - this will start a new thread for loading the data
    }

    public static MensaBot getInstance() {
        return instance;
    }

    @Override
    public JDA getJda() {
        return jda;
    }

    @Override
    public CommandHandler getCmdHandler() {
        return commandHandler;
    }
}
