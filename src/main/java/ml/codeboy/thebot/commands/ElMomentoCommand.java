package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.events.SlashCommandCommandEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ElMomentoCommand extends Command {

    public ElMomentoCommand() {
        super("ElMomentoDelMogus", "El Momento del mogus", "elmomento", "mogus");
    }

    @Override
    public void run(CommandEvent event) {
        event.reply("https://tenor.com/view/el-momento-del-mogus-amog-mogus-el-momento-gif-22890012");
    }
}
