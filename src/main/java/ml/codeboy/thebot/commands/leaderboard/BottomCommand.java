package ml.codeboy.thebot.commands.leaderboard;

import ml.codeboy.thebot.apis.mongoDB.DatabaseUserAPI;
import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.data.UserData;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.List;

public class BottomCommand extends Command {
    private final LeaderBoard leaderBoard;
    private final MessageEmbed loading;
    private MessageEmbed lastBottom;

    public BottomCommand(LeaderBoard leaderBoard) {
        super(leaderBoard.getName() + "Bottom", leaderBoard.getCurrency() + " Schlechtestenliste");
        setGuildOnlyCommand(false);
        this.leaderBoard = leaderBoard;
        loading = new EmbedBuilder().setTitle("Loading " + leaderBoard.getName() + "Bottom")
                .setDescription("please wait a few seconds\nI will update the message when I'm done").setColor(Color.RED).build();
        lastBottom = loading;
        setRequiredBotPermisions(Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION);
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(lastBottom);
        new Thread(() -> update(event)).start();
    }

    private void update(CommandEvent event) {
        Guild guild = event.getGuild();
        JDA jda = event.getJDA();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(leaderBoard.getName() + "Bottom");
        List<UserData> sorted = DatabaseUserAPI.getBottomN(leaderBoard.getCurrency(), 10);
        int i = 0;
        for (UserData d : sorted) {
            builder.addField(i++ + ".", d.getTag(jda) + " " + leaderBoard.getValue(d), false);
        }
        event.edit(builder);
        lastBottom = builder.build();
    }

}
