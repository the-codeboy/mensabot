package ml.codeboy.thebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class MensaBot implements Bot {

    private static MensaBot instance;//I don't really want to make this a singleton so I didn't make this final
    private final CommandHandler commandHandler;
    private final JDA jda;
    private final Logger logger
            = LoggerFactory.getLogger(getClass());

    public MensaBot() throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault(Config.getInstance().token)
                .enableIntents(GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_PRESENCES)
                .enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY).build();
        jda.awaitReady();
        commandHandler = new CommandHandler(this);
        instance = this;
        logger.info("Bot started");
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        new MensaBot();
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
