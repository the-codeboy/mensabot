package ml.codeboy.thebot.commands.leaderboard;

import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.data.UserData;
import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public class LeaderBoardCommand extends Command {
    private final LeaderBoard leaderBoard;

    public LeaderBoardCommand(LeaderBoard leaderBoard) {
        super(leaderBoard.getName(), "gives the " + leaderBoard.getCurrency() + " of a user");
        setGuildOnlyCommand(false);
        this.leaderBoard = leaderBoard;
        setRequiredBotPermisions(Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_HISTORY, Permission.MESSAGE_ADD_REACTION);
    }

    @Override
    public SlashCommandData getCommandData() {
        return super.getCommandData().addOption(OptionType.USER, "user", "the user to get to get the " + leaderBoard.getCurrency() + " of", false);
    }

    @Override
    public void run(CommandEvent event) {
        User user = event.getUser();
        if (event.isSlashCommandEvent()) {
            OptionMapping om = event.getSlashCommandEvent().getOption("user");
            if (om != null)
                user = om.getAsMember().getUser();
        } else if (event.isMessageEvent()) {
            List<Member> members = event.getMessageReceivedEvent().getMessage().getMentions().getMembers();
            if (!members.isEmpty() && members.get(0) != null)
                user = members.get(0).getUser();
        }
        UserData data = UserDataManager.getInstance().getData(user);
        int value = leaderBoard.getValue(data);
        EmbedBuilder builder = event.getBuilder();
        builder.setTitle(leaderBoard.getCurrency() + " of " + user.getAsTag())
                .setDescription(value + "");
        event.reply(builder);
    }
}