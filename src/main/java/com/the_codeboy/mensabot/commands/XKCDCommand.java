package com.the_codeboy.mensabot.commands;

import com.the_codeboy.mensabot.apis.Xkcd;
import com.the_codeboy.mensabot.apis.XkcdApi;
import com.the_codeboy.mensabot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.time.ZoneId;

public class XKCDCommand extends Command {

    public XKCDCommand() {
        super("xkcd", "Get any xkcd (number or random)");
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData()
                .addOptions(new OptionData(OptionType.INTEGER, "number",
                        "the number of the comic (1," + XkcdApi.getInstance().getMax() + ")",
                        false, false)
                        .setRequiredRange(1, XkcdApi.getInstance().getMax())
                );
    }

    @Override
    public void run(CommandEvent event) {
        int number;
        if (event.isSlashCommandEvent()) {
            SlashCommandInteractionEvent e = event.getSlashCommandEvent();
            OptionMapping option = e.getOption("number");
            if (option != null) {
                number = option.getAsInt();
            } else {
                number = 0;
            }
        } else {
            String[] args = event.getArgs();
            if (args.length == 0) {
                number = 0;
            } else if (args[0].equals("newest")) {
                number = XkcdApi.getInstance().getMax();
            } else {
                number = Integer.parseInt(args[0]);
            }
        }
        Xkcd xkcd;
        if (number == 0) {
            xkcd = XkcdApi.getInstance().get();
        } else {
            number = number < 1 ? 1 : (Math.min(number, XkcdApi.getInstance().getMax()));
            xkcd = XkcdApi.getInstance().get(number);
        }
        EmbedBuilder builder = event.getBuilder();
        builder.setTitle(xkcd.getTitle(), "https://xkcd.com/" + number + "/")
                .setImage(xkcd.getImg())
                .setTimestamp(java.time.ZonedDateTime.of(Integer.parseInt(xkcd.getYear()), Integer.parseInt(xkcd.getMonth()), Integer.parseInt(xkcd.getDay()), 0, 0, 0, 0, ZoneId.of("US/Eastern")));
        event.reply(builder);
    }
}
