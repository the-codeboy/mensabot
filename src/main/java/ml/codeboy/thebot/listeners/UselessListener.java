package ml.codeboy.thebot.listeners;

import ml.codeboy.thebot.Bot;
import ml.codeboy.thebot.commands.LatexCommand;
import ml.codeboy.thebot.util.Replyable;
import ml.codeboy.thebot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivityOrderEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.math.BigInteger;

import static ml.codeboy.thebot.util.Util.*;

/**
 * Contains useless functionality that is just here for fun
 */
public class UselessListener extends ListenerAdapter {
    private final Emoji amogus, sus, giesl, downvote;

    public UselessListener(Bot bot) {
        JDA jda = bot.getJda();
        try {
            bot.getJda().awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        amogus = jda.getEmojiById("909891436625944646");
        sus = jda.getEmojiById("930765635913408532");
        giesl = jda.getEmojiById("923655475675947028");
        downvote = jda.getEmojiById("903336514644222033");
    }


    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Thread t = new Thread(() -> {
            evaluateMessage(event);
            detectLatex(event);
        });
        t.start();
        String msg = event.getMessage().getContentRaw().toLowerCase();
        if (msg.contains("mogus") || msg.contains("imposter") || msg.contains("among us")) {
            if (amogus != null) event.getMessage().addReaction(amogus).queue();
        }
        if (msg.contains("sus")) {
            if (amogus != null) event.getMessage().addReaction(amogus).queue();
            if (sus != null) event.getMessage().addReaction(sus).queue();
        }
        if (msg.contains("giesl") || msg.contains("weihnacht")) {
            if (giesl != null) event.getMessage().addReaction(giesl).queue();
        }

        if (event.getAuthor().getId().equals("290368310711681024") && !event.getChannel().getId().equals("917201826271604736")) {
            event.getMessage().addReaction(downvote).queue();
        }
    }

    @Override
    public void onUserActivityStart(@NotNull UserActivityStartEvent event) {
        checkActivity(event.getMember());
    }

    @Override
    public void onUserUpdateActivityOrder(@NotNull UserUpdateActivityOrderEvent event) {
        checkActivity(event.getMember());
    }


    private void checkActivity(Member member) {
        for (Activity activity : member.getActivities()) {
            if (activity.isRich()) {
                RichPresence presence = activity.asRichPresence();
                if (presence != null && "401518684763586560".equals(presence.getApplicationId())
                        && presence.getLargeImage() != null && presence.getLargeImage().getText() != null) {
                    String message = null;
                    switch (presence.getLargeImage().getText()) {
                        case "Yuumi":
                            message = "Warum spielst du überhaupt League of Legends, wenn alles was du tust e-drücken ist?";
                            break;
                        case "Teemo":
                            message = "Hör mal auf Teemo zu spielen. Scheiß range top laner";
                            break;
                    }
                    if (message != null && Math.random() > 0.8) {
                        member.getUser().openPrivateChannel().complete().sendMessage(message).queue();
                    }
                }

            }
        }
    }


    private void evaluateMessage(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        if (!event.getJDA().getSelfUser().getId().equals(event.getAuthor().getId()) && content.endsWith("=?")) {
            try {
                String text = content.substring(0, content.length() - 2);
                if (isKAN(text)) {
                    BigInteger i = null;
                    try {
                        i = getKANValue(text);
                    } catch (Throwable e) {
                        // Catches all throwables instead of only exceptions to include stackoverflow
                        // and other errors.
                        // They would not crash the bot if not caught, but this makes sure the user is
                        // notified that their number will no longer be calculated
                        e.printStackTrace();
                        event.getMessage().replyEmbeds(new EmbedBuilder().setColor(Color.RED).setTitle("I am unable to calculate this number :(").build()).queue();
                        return;
                    }
                    String prefix = text + " = ";
                    int charsLeft = 2000 - prefix.length();
                    String result = i.toString();
                    result = Util.toDigits(charsLeft, result);
                    MessageAction action = event.getMessage().reply(prefix + result);
                    if (!result.equals(i.toString()))
                        action = action.addFile(Util.toDigits(1048576, i.toString()).getBytes(), "number.txt");
                    action.queue();
                    return;
                }
                double i = evaluate(text);
                event.getChannel().sendMessage(text + " = " + i).queue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void detectLatex(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        if (content.startsWith("```tex\n") && content.endsWith("```")) {
            content = content.substring(7, content.length() - 3);
        } else if (content.startsWith("```latex\n") && content.endsWith("```")) {
            content = content.substring(9, content.length() - 3);
        } else return;
        LatexCommand.respondLatex(content, Replyable.from(event.getMessage()));
    }

}
