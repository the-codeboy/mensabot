package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public class MensaAnnounceChannelCommand extends Command {
    public MensaAnnounceChannelCommand() {
        super("mensaAnnounceChannel", "Sets the channel where the bot will announce the current mensa meals daily");
        setRequiredPermissions(Permission.MANAGE_SERVER);
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.CHANNEL, "channel", "the channel to post the meals in");
    }

    @Override
    public void run(CommandEvent event) {
        if (event.isMessageEvent()) {
            List<TextChannel> channels = event.getMessageReceivedEvent().getMessage().getMentionedChannels();
            if (channels.isEmpty()) {
                deactivate(event);
            } else {
                setUpdateChannel(event, channels.get(0));
            }
        } else {
            List<OptionMapping> options = event.getSlashCommandEvent().getOptions();
            MessageChannel channel = null;
            if (!options.isEmpty()) {
                channel = options.get(0).getAsMessageChannel();
            }
            if (channel == null) {
                deactivate(event);
            } else {
                setUpdateChannel(event, channel);
            }
        }
    }

    private void deactivate(CommandEvent event) {
        event.reply("Deactivating meal announcement");//todo fix
        event.getGuildData().setUpdateChannelId("");
    }

    private void setUpdateChannel(CommandEvent event, MessageChannel channel) {
        event.getGuildData().setUpdateChannelId(channel.getId());
        event.reply("Meals will be sent to " + channel.getAsMention());

    }
}
