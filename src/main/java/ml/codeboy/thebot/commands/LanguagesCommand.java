package ml.codeboy.thebot.commands;

import com.github.codeboy.piston4j.api.Piston;
import com.github.codeboy.piston4j.api.Runtime;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

public class LanguagesCommand extends Command{
    public LanguagesCommand() {
        super("languages", "sends available languages");
    }

    @Override
    public void run(CommandEvent event) {
        EmbedBuilder builder=new EmbedBuilder();
        for (Runtime r:Piston.getDefaultApi().getRuntimes()){
            builder.addField(r.getLanguage(),r.getVersion(),true);
        }
        event.reply(builder);
    }
}
