package ml.codeboy.thebot;

import com.github.codeboy.api.Mensa;
import ml.codeboy.thebot.apis.AdviceApi;
import ml.codeboy.thebot.commands.*;
import ml.codeboy.thebot.commands.quotes.AddQuote;
import ml.codeboy.thebot.commands.quotes.QuoteCommand;
import ml.codeboy.thebot.commands.secret.LoadKarma;
import ml.codeboy.thebot.commands.secret.Msg;
import ml.codeboy.thebot.commands.secret.React;
import ml.codeboy.thebot.commands.secret.RickRoll;
import ml.codeboy.thebot.commands.sound.Queue;
import ml.codeboy.thebot.commands.sound.*;
import ml.codeboy.thebot.data.GuildData;
import ml.codeboy.thebot.data.GuildManager;
import ml.codeboy.thebot.events.MessageCommandEvent;
import ml.codeboy.thebot.events.SlashCommandCommandEvent;
import ml.codeboy.thebot.quotes.Quote;
import ml.codeboy.thebot.quotes.QuoteManager;
import ml.codeboy.thebot.tracker.BedTimeTracker;
import ml.codeboy.thebot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommandHandler extends ListenerAdapter {
    private final Bot bot;
    private final HashMap<String, Command> commands = new HashMap<>();
    private final ArrayList<Command> allCommands = new ArrayList<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private Guild server;

    public CommandHandler(Bot bot) {
        this.bot = bot;
        bot.getJda().addEventListener(this);
        String serverID = Config.getInstance().serverId;
        try {
            bot.getJda().awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (serverID != null)
            server = bot.getJda().getGuildById(serverID);
        amogus=getBot().getJda().getEmoteById("909891436625944646");
        sus=getBot().getJda().getEmoteById("930765635913408532");

        this.registerKnowCommands();

        registerAnnouncements();
        registerBedTimeTracker();
    }

    private void registerBedTimeTracker() {
        BedTimeTracker tracker = new BedTimeTracker(getBot());
    }

    private void registerAnnouncements() {
        Date date = new Date();
        announceIn(60 * 60 * 24 - (date.getSeconds() + date.getMinutes() * 60 + date.getHours() * 3600) + 10);//10 seconds extra
    }

    private void announceIn(int seconds) {
        executorService.schedule(() -> {
            registerAnnouncements();
            sendToAllGuilds();
        }, seconds, TimeUnit.SECONDS);
    }

    private void sendToAllGuilds() {
        MensaBot.logger.info("Sending meals to guilds");
        for (Guild guild : getBot().getJda().getGuilds()) {
            sendToGuild(guild);
        }
    }

    private void sendToGuild(Guild guild) {
        GuildData data = GuildManager.getInstance().getData(guild);
        try {
            Mensa mensa = data.getDefaultMensa();
            MessageChannel channel = (MessageChannel) getBot().getJda().getGuildChannelById(data.getUpdateChannelId());
            if (channel != null) {
                try {
                    channel.retrieveMessageById(data.getLatestAnnouncementId()).queue((message) -> {
                        message.delete().reason("idk");
                    });
                } catch (Exception ignored) {
                }
                Message message = channel.sendMessageEmbeds(MensaUtil.MealsToEmbed(mensa, new Date()).build()).complete();
                data.setLatestAnnouncementId(message.getId());
            }
        } catch (Exception ignored) {
        }
    }

    public void registerKnowCommands() {
        this.registerCommand(new Help(bot));

        registerCommand(new ChuckNorrisJokeCommand());
        registerCommand(new TrumpQuoteCommand());
        registerCommand(new AdviceCommand());
        registerCommand(new NewsCommand());
        registerCommand(new InsultCommand());
        registerCommand(new RhymeCommand());

        registerCommand(new MensaCommand());
        registerCommand(new DefaultMensaCommand());
        registerCommand(new MensaAnnounceChannelCommand());

        registerCommand(new ExecuteCommand());
        registerCommand(new LanguagesCommand());

        registerAudioCommands();

        registerCommand(new AddQuote());
        registerCommand(new QuoteCommand());
        registerCommand(new Karma());
        registerCommand(new KarmaTop());
        registerCommand(new KarmaBottom());

        registerSecretCommands();

        registerAllSlashCommands();

        if (Config.getInstance().quoteStatus) {
            changeStatus();
        }
    }

    private void registerSecretCommands() {
        registerCommand(new RickRoll());
        registerCommand(new React());
        registerCommand(new Msg());
        registerCommand(new LoadKarma());
    }

    private void changeStatus() {
        new Timer().schedule(new TimerTask() {
            public void run() {
                String status;
                do {
                    status = getRandomStatus();
                } while (status.length() > 128 || status.length() == 0);
                getBot().getJda().getPresence().setActivity(Activity.of(Activity.ActivityType.STREAMING, status, "https://www.youtube.com/watch?v=dQw4w9WgXcQ&v=watch&feature=youtu.be"));
            }
        }, 0, 5 * 60_000);
    }

    private String getRandomStatus() {
        return getRandomQuoteStatus();
    }

    private String getRandomAdviceStatus() {
        return AdviceApi.getInstance().getObject();
    }

    private String getRandomQuoteStatus() {
        Quote quote;
        String status;
        quote = QuoteManager.getInstance().getRandomQuote("Sun Tzu");
        if (quote == null)
            return "";
        status = "\"" + quote.getContent() +
                "\"\n - " + quote.getPerson();
        return status;
    }

    private void registerAudioCommands() {
        registerCommand(new Play());
        registerCommand(new Pause());
//        registerCommand(new Echo());
        registerCommand(new Loop());
        registerCommand(new PlayNext());
        registerCommand(new Queue());
        registerCommand(new RemoveTrack());
        registerCommand(new Shuffle());
        registerCommand(new Skip());
        registerCommand(new Stop());
        registerCommand(new Volume());
        registerCommand(new CurrentTrack());
    }

    private void registerCommand(Command command) {
        command.register(this);
        commands.put(command.getName().toLowerCase(), command);
        allCommands.add(command);
        for (String alias : command.getAliases()) {
            commands.put(alias.toLowerCase(), command);
        }
        CommandData data = command.getCommandData();
        if (data != null && !command.isHidden())
            registerSlashCommand(data);
        MensaBot.logger.info("registered command " + command.getName());
    }

    private final Emote amogus,sus;

    private void amogus(MessageReceivedEvent event){
        String msg=event.getMessage().getContentRaw().toLowerCase();
        if(msg.contains("mogus")||msg.contains("imposter")||msg.contains("among us")){
            MensaBot.logger.info("amogus");
            event.getMessage().addReaction(amogus).queue();
        }
        if(msg.contains("sus")) {
            MensaBot.logger.info("sus");
            event.getMessage().addReaction(amogus).queue();
            event.getMessage().addReaction(sus).queue();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        amogus(event);
        String content = event.getMessage().getContentRaw();
        if (!event.isFromGuild()){
            TextChannel channel= (TextChannel) getBot().getJda().getGuildChannelById("966789128375140412");
            channel.sendMessageEmbeds(new EmbedBuilder().setAuthor(event.getAuthor().getAsTag()+" "+event.getAuthor().getAsMention())
                    .setDescription(content).setThumbnail(event.getAuthor().getAvatarUrl()).build()).queue();
            return;
        }
        if (!content.startsWith(Config.getInstance().prefix))
            return;
        content = content.replaceFirst(Config.getInstance().prefix, "");
        String cmd = content.split(" ", 2)[0];
        Command command = getCommand(cmd);
        if (command != null) {
            MensaBot.logger.info(event.getGuild().getName() + ": " + event.getChannel().getName() + ": " + event.getAuthor().getAsTag()
                    + ": " + event.getMessage().getContentRaw());
            command.execute(new MessageCommandEvent(event));
        }
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Command command = getCommand(event.getName());
        if (command != null) {
            MensaBot.logger.info(event.getGuild().getName() + ": " + event.getChannel().getName() + ": " + event.getMember().getUser().getAsTag()
                    + ": " + event.getCommandString());
            command.execute(new SlashCommandCommandEvent(event));
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        Command command = getCommand(event.getName());
        if (command != null) {
            command.autoComplete(event);
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (event.getGuild().getId().equals("896116435875668019")) {
            TextChannel channel = (TextChannel) event.getGuild().getGuildChannelById("896116435875668024");
            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("Wilkommen " + event.getMember().getEffectiveName())
                    .setDescription(event.getMember().getAsMention() + "Bitte ändere deinen Nickname auf dem Server zu deinem echten Namen: Das macht die Kommunikation etwas leichter.")
                    .setColor(Util.getRandomColor());

            channel.sendMessageEmbeds(builder.build()).queue();
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        MessageReaction.ReactionEmote reaction = event.getReaction().getReactionEmote();
        if (reaction.isEmote()) {
            String id = reaction.getEmote().getId();
            boolean upvote = Config.getInstance().isUpvote(id),
                    downVote = Config.getInstance().isDownvote(id);
            if (upvote || downVote) {
                Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
                Util.addKarma(message.getAuthor(), upvote ? 1 : -1);
            }
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        MessageReaction.ReactionEmote reaction = event.getReaction().getReactionEmote();
        if (reaction.isEmote()) {
            String id = reaction.getEmote().getId();
            boolean upvote = Config.getInstance().isUpvote(id),
                    downVote = Config.getInstance().isDownvote(id);
            if (upvote || downVote) {
                Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
                Util.addKarma(message.getAuthor(), upvote ? -1 : 1);//removing upvotes => remove karma
            }
        }
    }

    private void registerAllSlashCommands() {
        CommandListUpdateAction action = getBot().getJda().updateCommands();
        for (Command command : allCommands) {
            if (!command.isHidden())
                action = action.addCommands(command.getCommandData());
        }
        action.queue();
    }

    private void registerSlashCommand(CommandData data) {
        if (getServer() != null)
            getServer().upsertCommand(data).queue();
//        getBot().getJda().upsertCommand(data);
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        if (event.getChannelLeft().getMembers().size() == 1) {
            AudioChannel connectedChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();
            if (connectedChannel != event.getChannelLeft())
                return;

            PlayerManager.getInstance().destroy(event.getGuild());
        }
    }

    private Bot getBot() {
        return bot;
    }

    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public Collection<Command> getCommands() {
        return allCommands;
    }

    public Guild getServer() {
        return server;
    }
}
