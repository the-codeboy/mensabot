package ml.codeboy.thebot.commands;

import com.github.codeboy.OpenMensa;
import com.github.codeboy.api.Mensa;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;

public class DefaultMensaCommand extends Command {
    public DefaultMensaCommand() {
        super("default_mensa", "Sets the default mensa for this server");
        setRequiredPermissions(Permission.MANAGE_SERVER);
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.INTEGER, "id", "Id of the new default Mensa", true);
    }

    @Override
    public void run(CommandEvent event) {
        String[] args = event.getArgs();
        if (args.length == 0) {
            event.replyError("Missing argument");
        } else {
            try {
                int i = Integer.parseInt(args[0]);
                Mensa mensa = OpenMensa.getInstance().getMensa(i);
                if (mensa == null) {
                    event.replyError("Unable to find mensa with id " + i);
                } else {
                    event.reply(new EmbedBuilder().setTitle("Success").setDescription("New default mensa for server is " + mensa.getName())
                            .setColor(Color.GREEN));
                    event.getGuildData().setDefaultMensaId(i);
                }
            } catch (NumberFormatException e) {
                event.replyError("Invalid argument. Expected Integer");
            }
        }
    }
}
