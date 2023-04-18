package ml.codeboy.thebot.commands.nils;

import ml.codeboy.thebot.events.CommandEvent;

public class ElMomentoCommand extends NilsCommand {

    public ElMomentoCommand() {
        super("ElMomentoDelMogus", "El Momento del mogus", "elmomento", "mogus");
    }

    @Override
    public void run(CommandEvent event) {
        event.reply("https://tenor.com/view/el-momento-del-mogus-amog-mogus-el-momento-gif-22890012");
    }
}
