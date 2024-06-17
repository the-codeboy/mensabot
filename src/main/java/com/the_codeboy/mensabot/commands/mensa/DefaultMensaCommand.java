package com.the_codeboy.mensabot.commands.mensa;

import com.github.codeboy.OpenMensa;
import com.github.codeboy.api.Mensa;
import com.the_codeboy.mensabot.commands.Command;
import com.the_codeboy.mensabot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class DefaultMensaCommand extends Command {
    public DefaultMensaCommand() {
        super("default_mensa", "Sets the default mensa for this server");
        setRequiredPermissions(Permission.MANAGE_SERVER);
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.STRING, "name", "name of the new default Mensa", true,true);
    }

    @Override
    public void autoComplete(String option, List<String> options) {
        switch (option) {
            case "name": {
                for (Mensa mensa : OpenMensa.getInstance().getAllCanteens()) {
                    if (mensa != null)
                        options.add(mensa.getName());
                }
                break;
            }
        }
    }

    @Override
    public void run(CommandEvent event) {
        String[] args = event.getArgs();
        if (args.length == 0) {
            event.replyError("Missing argument");
        } else {
            try {
                String mensaName = String.join("", args);
                Optional<Mensa> optionalMensa = OpenMensa.getInstance().getAllCanteens().stream().filter(m -> m.getName().equals(mensaName)).findAny();
                if (!optionalMensa.isPresent()) {
                    event.replyError("Unable to find mensa " + mensaName);
                } else {
                    Mensa mensa = optionalMensa.get();
                    event.reply(new EmbedBuilder().setTitle("Success").setDescription("New default mensa for server is " + mensa.getName())
                            .setColor(Color.GREEN));
                    event.getGuildData().setDefaultMensaId(mensa.getId());
                }
            } catch (NumberFormatException e) {
                event.replyError("Invalid argument. Expected Integer");
            }
        }
    }
}
