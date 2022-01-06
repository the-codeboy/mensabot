package ml.codeboy.thebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class MensaBot implements Bot{

    private CommandHandler commandHandler;
    private JDA jda;

    @Override
    public JDA getJda() {
        return jda;
    }

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
