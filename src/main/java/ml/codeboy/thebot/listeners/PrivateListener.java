package ml.codeboy.thebot.listeners;

import ml.codeboy.thebot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static ml.codeboy.thebot.util.Util.evaluate;

/**
 * This class is for things on a private server of my friends
 */
public class PrivateListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        counter(event);
    }


    private void counter(MessageReceivedEvent event) {
        if (event.getChannel().getId().equals("898271566880727130")
                && !event.getJDA().getSelfUser().getId().equals(event.getAuthor().getId())) {
            try {
                double i = evaluate(event.getMessage().getContentRaw());
                if (Double.isNaN(i)) return;
                event.getChannel().sendMessage(i + 1 + "").queue();
            } catch (Exception ignored) {
            }
        }
    }


    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (event.getGuild().getId().equals("896116435875668019")) {
            TextChannel channel = (TextChannel) event.getGuild().getGuildChannelById("896116435875668024");
            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("Wilkommen " + event.getMember().getEffectiveName())
                    .setDescription(event.getMember().getAsMention()
                            + " Bitte ändere deinen Nickname auf dem Server zu deinem echten Namen: Das macht die Kommunikation etwas leichter.")
                    .setColor(Util.getRandomColor());

            channel.sendMessageEmbeds(builder.build()).queue();
        }
    }
}
