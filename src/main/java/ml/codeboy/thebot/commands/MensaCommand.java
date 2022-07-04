package ml.codeboy.thebot.commands;

import com.github.codeboy.OpenMensa;
import com.github.codeboy.api.Mensa;
import ml.codeboy.thebot.MensaUtil;
import ml.codeboy.thebot.apis.RWTHMensa;
import ml.codeboy.thebot.data.GuildManager;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class MensaCommand extends Command {
    public MensaCommand() {
        super("mensa", "Sends the current food in mensa Academica", "food");
        OpenMensa.getInstance().reloadCanteens();//doesn't work without this
        Collection<Mensa> mensas = OpenMensa.getInstance().searchMensa("aachen");

        for (Mensa mensa : mensas) {
            OpenMensa.getInstance().addMensa(new RWTHMensa(mensa));
        }
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData()
                .addOption(OptionType.INTEGER, "mensa_id", "The id of the mensa eg 187", false)
                .addOption(OptionType.STRING, "date", "The date", false, true)
                .addOption(OptionType.STRING, "name", "Name of the mensa", false, true);
    }

    @Override
    public void autoComplete(String option, List<String> options) {
        switch (option) {
            case "date": {
                options.add("gestern");
                options.add("heute");
                options.add("morgen");
                options.add("übermorgen");
                break;
            }
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
            sendDefaultMenu(event);
        } else if (event.isMessageEvent()) {
            Mensa mensa;

            try {
                int mensaId = Integer.parseInt(args[0]);

                mensa = OpenMensa.getInstance().getMensa(mensaId);
            } catch (NumberFormatException e) {
                mensa = tryGetMensa(event, args[0]);
                if (mensa == null)
                    return;
            }

            Date date = MensaUtil.wordToDate(args[1]);
            if (date == null) {
                event.replyError("Invalid argument: " + args[1] + ". Expected one of: yesterday, today, tomorrow");
            } else {
                sendMensaMenu(event, mensa, date);
            }

        } else {
            SlashCommandInteractionEvent sce = event.getSlashCommandEvent();
            Mensa mensa = GuildManager.getInstance().getData(event.getGuild()).getDefaultMensa();
            Date date = new Date();
            for (OptionMapping om : sce.getOptions()) {
                if (om.getName().equals("mensa_id")) {
                    mensa = OpenMensa.getInstance().getMensa((int) om.getAsLong());
                } else if (om.getName().equals("date")) {
                    date = MensaUtil.wordToDate(om.getAsString());
                    if (date == null) {
                        event.replyError("Invalid argument: " + om.getAsString() + ". Expected one of: yesterday, today, tomorrow");
                        return;
                    }
                } else if (om.getName().equals("name")) {
                    mensa = tryGetMensa(event, om.getAsString());
                    if (mensa == null)
                        return;
                }
            }
            sendMensaMenu(event, mensa, date);
        }
    }

    private Mensa tryGetMensa(CommandEvent event, String query) {
        List<Mensa> mensas = OpenMensa.getInstance().searchMensa(query);
        if (mensas.size() == 0) {
            event.replyError("No mensas found matching " + query);
        } else if (mensas.size() == 1) {
            return mensas.get(0);
        } else {
            sendMensas(event, mensas, query);
        }
        return null;
    }

    private void sendMensas(CommandEvent event, List<Mensa> mensas, String query) {
        EmbedBuilder builder = event.getBuilder();

        builder.setTitle("Mensas matching " + query);

        int defaultMensaId = event.getGuildData().getDefaultMensaId();

        for (Mensa mensa : mensas) {
            builder.addField(mensa.getName(), "id: " + mensa.getId() + (mensa.getId() == defaultMensaId ? "\nThis servers default mensa" : "")
                    , true);
            if (!builder.isValidLength()) {
                builder.getFields().remove(builder.getFields().size() - 1);

                event.reply(builder);
                return;
            }
        }

        event.reply(builder);
    }

    private void sendDefaultMenu(CommandEvent event) {
        Mensa mensa;
        if (event.getGuild() == null)
            mensa = OpenMensa.getInstance().getMensa(187);
        else
            mensa = GuildManager.getInstance().getData(event.getGuild()).getDefaultMensa();
        sendMensaMenu(event, mensa);
    }

    private void sendMensaMenu(CommandEvent event, Mensa mensa) {
        sendMensaMenu(event, mensa, new Date());
    }

    private void sendMensaMenu(CommandEvent event, Mensa mensa, Date date) {
        if (!mensa.isOpen(date)) {
            event.replyError("The mensa " + mensa.getName() + " is not open " + MensaUtil.dateToWord(date));
            return;
        }
        event.reply(MensaUtil.MealsToEmbed(mensa, date));
    }
}
