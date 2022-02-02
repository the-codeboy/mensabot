package ml.codeboy.thebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class MensaBot implements Bot {

    public static final Logger logger
            = LoggerFactory.getLogger(MensaBot.class);
    private CommandHandler commandHandler;
    private JDA jda;

    public MensaBot() throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault(Config.getInstance().token)
                .enableIntents(GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_PRESENCES)
                .enableCache(CacheFlag.ONLINE_STATUS).build();
        jda.awaitReady();
        commandHandler = new CommandHandler(this);
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        new MensaBot();
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
