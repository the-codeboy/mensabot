package ml.codeboy.thebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class MensaBot implements Bot{

    private CommandHandler commandHandler;
    private JDA jda;

    @Override
    public JDA getJda() {
        return jda;
    }

    public static final Logger logger
            = LoggerFactory.getLogger(MensaBot.class);

    @Override
    public CommandHandler getCmdHandler() {
        return commandHandler;
    }

    public MensaBot() throws LoginException, InterruptedException {
        jda= JDABuilder.createDefault(Config.getInstance().token).build();
        jda.awaitReady();
        commandHandler=new CommandHandler(this);
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        new MensaBot();
    }
}
