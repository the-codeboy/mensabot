package ml.codeboy.thebot.commands.leaderboard;

import ml.codeboy.thebot.apis.mongoDB.DatabaseUserAPI;
import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.data.UserData;
import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TopCommand extends Command {
    private final LeaderBoard leaderBoard;
    private final MessageEmbed loading;
    private MessageEmbed lastTop;

    public TopCommand(LeaderBoard leaderBoard) {
        super(leaderBoard.getName() + "Top", leaderBoard.getCurrency() + " Bestenliste");
        setGuildOnlyCommand(false);
        this.leaderBoard = leaderBoard;
        loading = new EmbedBuilder().setTitle("Loading " + leaderBoard.getName() + "Top")
                .setDescription("please wait a few seconds\nI will update the message when I'm done").setColor(Color.RED).build();
        lastTop = loading;
        setRequiredBotPermisions(Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION);
    }

    @Override
    public void run(CommandEvent event) {
        event.reply(lastTop);
        new Thread(() -> update(event)).start();
    }

    private void update(CommandEvent event) {
        Guild guild = event.getGuild();
        JDA jda = event.getJDA();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(leaderBoard.getName() + "Top");
        List<UserData> sorted = DatabaseUserAPI.getTopN(leaderBoard.getCurrency(), 10);//new ArrayList<>(UserDataManager.getInstance().getAllUserData());
        int i = 0;
        for (UserData d : sorted) {
            builder.addField(i++ + ".", d.getTag(jda) + " " + leaderBoard.getValue(d), false);
        }
        event.edit(builder);
        lastTop = builder.build();
    }


}
