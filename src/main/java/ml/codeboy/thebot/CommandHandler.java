package ml.codeboy.thebot;

import com.github.codeboy.api.Mensa;
import com.github.codeboy.jokes4j.Jokes4J;
import com.github.codeboy.jokes4j.api.Flag;
import com.github.codeboy.jokes4j.api.JokeRequest;
import ml.codeboy.met.Weather4J;
import ml.codeboy.met.data.Forecast;
import ml.codeboy.thebot.apis.AdviceApi;
import ml.codeboy.thebot.commands.*;
import ml.codeboy.thebot.commands.debug.GetQuotes;
import ml.codeboy.thebot.commands.debug.ListQuotes;
import ml.codeboy.thebot.commands.image.MorbCommand;
import ml.codeboy.thebot.commands.image.ShitCommand;
import ml.codeboy.thebot.commands.image.meme.*;
import ml.codeboy.thebot.commands.leaderboard.LeaderBoard;
import ml.codeboy.thebot.commands.mensa.*;
import ml.codeboy.thebot.commands.nils.ElMomentoCommand;
import ml.codeboy.thebot.commands.quotes.AddQuote;
import ml.codeboy.thebot.commands.quotes.AddQuoteList;
import ml.codeboy.thebot.commands.quotes.QuoteCommand;
import ml.codeboy.thebot.commands.secret.*;
import ml.codeboy.thebot.commands.sound.Queue;
import ml.codeboy.thebot.commands.sound.*;
import ml.codeboy.thebot.data.GuildData;
import ml.codeboy.thebot.data.GuildManager;
import ml.codeboy.thebot.data.UserDataManager;
import ml.codeboy.thebot.events.MessageCommandEvent;
import ml.codeboy.thebot.events.SlashCommandCommandEvent;
import ml.codeboy.thebot.quotes.Quote;
import ml.codeboy.thebot.quotes.QuoteManager;
import ml.codeboy.thebot.tracker.BedTimeTracker;
import ml.codeboy.thebot.util.ButtonListener;
import ml.codeboy.thebot.util.ModalListener;
import ml.codeboy.thebot.util.SelectMenuListener;
import ml.codeboy.thebot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivityOrderEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.jetbrains.annotations.NotNull;
import org.mariuszgromada.math.mxparser.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;

import static ml.codeboy.thebot.WeatherUtil.generateForecastImage;
import static ml.codeboy.thebot.util.Util.getKANValue;
import static ml.codeboy.thebot.util.Util.isKAN;

public class CommandHandler extends ListenerAdapter {
    private final Logger logger
            = LoggerFactory.getLogger(getClass());
    private final Bot bot;
    private final HashMap<String, Command> commands = new HashMap<>();
    private final ArrayList<Command> allCommands = new ArrayList<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private Guild server;
    private final HashMap<String, SelectMenuListener> selectMenuListeners = new HashMap<>();
    private final HashMap<String, ButtonListener> buttonListeners = new HashMap<>();
    private final HashMap<String, ModalListener> modalListeners = new HashMap<>();

    private final Emoji amogus, sus, downvote;
    private final Emoji giesl;

    private ExecutorService executor=Executors.newFixedThreadPool(10);

    private void registerBedTimeTracker() {
        BedTimeTracker tracker = new BedTimeTracker(getBot());
    }

    private void registerAnnouncements() {
        Date date = new Date();
        announceIn(60 * 60 * 20 - (date.getSeconds() + date.getMinutes() * 60 + date.getHours() * 3600), false);
        announceIn(60 * 60 * 7 - (date.getSeconds() + date.getMinutes() * 60 + date.getHours() * 3600), true);
    }

    private void announceIn(int seconds, boolean includeWeather) {
        if (seconds < 0)
            seconds += 24 * 60 * 60;
        executorService.scheduleAtFixedRate(() -> {
            sendMealsToAllGuilds();
            if (includeWeather)
                sendWeatherToAllGuilds();
        }, seconds, 24 * 60 * 60, TimeUnit.SECONDS);
    }

    public CommandHandler(Bot bot) {
        UserDataManager.getInstance();//to load userdata - this will start a new thread for loading the data
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
        amogus = getBot().getJda().getEmojiById("909891436625944646");
        sus = getBot().getJda().getEmojiById("930765635913408532");
        downvote = getBot().getJda().getEmojiById("903336514644222033");
        giesl = getBot().getJda().getEmojiById("923655475675947028");

        this.registerKnowCommands();

        registerAnnouncements();
//        registerBedTimeTracker();

    }

    private void sendMealsToGuild(Guild guild) {
        GuildData data = GuildManager.getInstance().getData(guild);
        try {
            Mensa mensa = data.getDefaultMensa();
            MessageChannel channel = (MessageChannel) getBot().getJda().getGuildChannelById(data.getUpdateChannelId());
            if (channel != null) {
                Message message = channel.sendMessageEmbeds(MensaUtil.MealsToEmbed(mensa, new Date(System.currentTimeMillis() + 1000 * 3600 * 5)).build())
                        .setActionRows(MensaUtil.mealButtons).complete();
                data.setLatestAnnouncementId(message.getId());
                data.save();
            }
        } catch (Exception ignored) {
        }
    }

    private void sendMealsToAllGuilds() {
        logger.info("Sending meals to guilds");
        for (Guild guild : getBot().getJda().getGuilds()) {
            sendMealsToGuild(guild);
        }
    }

    private void sendWeatherToGuild(Guild guild) {
        GuildData data = GuildManager.getInstance().getData(guild);
        try {
            Mensa mensa = data.getDefaultMensa();
            MessageChannel channel = (MessageChannel) getBot().getJda().getGuildChannelById(data.getUpdateChannelId());
            if (channel != null) {
                List<Double> coordinates = mensa.getCoordinates();
                String lat = String.valueOf(coordinates.get(0)), lon = String.valueOf(coordinates.get(1));
                List<Forecast> forecasts = Weather4J.getForecasts(lat, lon);
                Instant now = Instant.now();
                while (forecasts.get(1).getTime().isBefore(now)) {
                    forecasts.remove(0);
                }

                BufferedImage image = generateForecastImage(forecasts, 16);
                File file = new File("images/" + new Random().nextInt() + ".png");
                try {
                    ImageIO.write(image, "png", file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                channel.sendMessage("Forecast for " + mensa.getCity() + "\nData from The Norwegian Meteorological Institute")
                        .addFile(file, "weather_forecast.png").complete();
                file.delete();
            }
        } catch (Exception ignored) {
        }
    }

    private void sendWeatherToAllGuilds() {
        logger.info("Sending weather to guilds");
        for (Guild guild : getBot().getJda().getGuilds()) {
            sendWeatherToGuild(guild);
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

    private void createCommand(Class<? extends Command>command){
        executor.execute(()-> {
            try {
                registerCommand(command.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("failed to create Command "+command.getName(),e);
            }
        });
    }


    public void registerKnowCommands() {
        this.registerCommand(new Help(bot));

        createCommand(ChuckNorrisJokeCommand.class);
        createCommand(JermaCommand.class);
        createCommand(TrumpQuoteCommand.class);
        createCommand(AdviceCommand.class);
        createCommand(NewsCommand.class);
        createCommand(InsultCommand.class);
        createCommand(RhymeCommand.class);
        createCommand(MemeCommand.class);
        createCommand(JokeCommand.class);
        createCommand(ShortsCommand.class);
        createCommand(WeatherCommand.class);
        createCommand(PingCommand.class);
        createCommand(ShittyTranslateCommand.class);
        createCommand(ASCIICommand.class);
        createCommand(GifCommand.class);

        createCommand(MensaCommand.class);
        createCommand(RateCommand.class);
        createCommand(DefaultMensaCommand.class);
        createCommand(MensaAnnounceChannelCommand.class);
        createCommand(DetailCommand.class);
        createCommand(AddImageCommand.class);

        createCommand(DönerrateCommand.class);
        createCommand(Dönertop.class);

        createCommand(ExecuteCommand.class);
        createCommand(LanguagesCommand.class);

        registerAudioCommands();

        createCommand(AddQuote.class);
        createCommand(AddQuoteList.class);
        createCommand(QuoteCommand.class);
//        registerCommand(new Karma());
//        registerCommand(new KarmaTop());
//        registerCommand(new KarmaBottom());

        registerLeaderBoardCommands();

        registerImageCommands();

        registerSecretCommands();

        registerNilsCommands();

        registerDebugCommands();

        if (Config.getInstance().quoteStatus) {
            changeStatus();
        }
        try {
            executor.shutdown();
            executor.awaitTermination(1,TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error("bot did not start in time",e);
        }

        registerAllSlashCommands();
    }

    private void registerImageCommands() {
        createCommand(MorbCommand.class);
        createCommand(ShitCommand.class);
        createCommand(ChangeMyMindCommand.class);
        createCommand(HotlineBlingCommand.class);
        createCommand(TwoButtonsCommand.class);
        createCommand(Draw25Command.class);
        createCommand(DisasterGirlCommand.class);
        createCommand(SupermanCommand.class);
    }

    private void registerSecretCommands() {
        createCommand(RickRoll.class);
        createCommand(React.class);
        createCommand(Msg.class);
        createCommand(LoadKarma.class);
        createCommand(LoadSusCount.class);
        createCommand(Bee.class);
        createCommand(AcceptImage.class);
        createCommand(RejectImage.class);
        createCommand(SendImageInfo.class);
    }

    private void registerNilsCommands()
    {
        createCommand(ElMomentoCommand.class);
    }

    private void registerDebugCommands() {
        createCommand(ListQuotes.class);
        createCommand(GetQuotes.class);
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
        }, 0, 60_000);
    }

    private String getRandomStatus() {
        return Jokes4J.getInstance().getJoke(new JokeRequest.Builder().blackList(Flag.explicit, Flag.nsfw, Flag.racist, Flag.sexist).build()).getJoke();
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
        createCommand(fPlay.class);
        createCommand(Pause.class);
        createCommand(Resume.class);
//        createCommand(Echo.class);
        createCommand(Loop.class);
        createCommand(PlayNext.class);
        createCommand(Queue.class);
        createCommand(RemoveTrack.class);
        createCommand(Shuffle.class);
        createCommand(Skip.class);
        createCommand(Stop.class);
        createCommand(Volume.class);
        createCommand(CurrentTrack.class);
        createCommand(Leave.class);
        createCommand(Join.class);
    }

    private void registerLeaderBoardCommands() {
        LeaderBoard.registerAll(this);
    }

    public void registerCommand(Command command) {
        command.register(this);
        commands.put(command.getName().toLowerCase(), command);
        allCommands.add(command);
        for (String alias : command.getAliases()) {
            commands.put(alias.toLowerCase(), command);
        }
        CommandData data = command.getCommandData();
        if (data != null && !command.isHidden())
            registerSlashCommand(data);
        logger.info("registered command " + command.getName());
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
                        logger.info(member.getUser().getAsTag() + " " + message);
                        member.getUser().openPrivateChannel().complete().sendMessage(message).queue();
                    }
                }

            }
        }
    }

    private void amogus(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw().toLowerCase();
        if (msg.contains("mogus") || msg.contains("imposter") || msg.contains("among us")) {
            logger.info("amogus");
            if (amogus != null)
                event.getMessage().addReaction(amogus).queue();
        }
        if (msg.contains("sus")) {
            logger.info("sus");
            if (amogus != null)
                event.getMessage().addReaction(amogus).queue();
            if (sus != null)
                event.getMessage().addReaction(sus).queue();
        }
        if (msg.contains("giesl")||msg.contains("weihnacht")) {
            logger.info("weihnachtsgiesl");
            if (giesl != null)
                event.getMessage().addReaction(giesl).queue();
        }

        if (event.getAuthor().getId().equals("290368310711681024") && !event.getChannel().getId().equals("917201826271604736")) {
            event.getMessage().addReaction(downvote).queue();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Thread t = new Thread(() -> {
            amogus(event);
            counter(event);
            evaluateMessage(event);
        });
        t.start();
        String content = event.getMessage().getContentRaw();
        if (!event.isFromGuild() && !event.getAuthor().isBot()) {
            TextChannel channel = (TextChannel) getBot().getJda().getGuildChannelById(Config.getInstance().dmDebugChannel);
            if (channel != null) {
                if (event.getMessage().getContentRaw().length() > 0)
                    channel.sendMessageEmbeds(new EmbedBuilder().setAuthor(event.getAuthor().getAsTag() + " " + event.getAuthor().getAsMention())
                            .setDescription(content).setThumbnail(event.getAuthor().getAvatarUrl()).setTimestamp(event.getMessage().getTimeCreated()).build()).queue();
                else {
                    for (Message.Attachment attachment : event.getMessage().getAttachments()) {
                        File file = new File("tmp" + File.separator + Math.random() + File.separator
                                + attachment.getFileName());
                        file.getParentFile().mkdirs();
                        try {
                            file = attachment.downloadToFile(file).get();
                            channel.sendMessageEmbeds(new EmbedBuilder().setAuthor(event.getAuthor().getAsTag() + " " + event.getAuthor().getAsMention())
                                    .setThumbnail(event.getAuthor().getAvatarUrl()).setTimestamp(event.getMessage().getTimeCreated()).build()).queue();
                            channel.sendFile(file).complete();
                            file.delete();
                            file.getParentFile().delete();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        if (!content.startsWith(Config.getInstance().prefix))
            return;
        content = content.replaceFirst(Config.getInstance().prefix, "");
        String cmd = content.split(" ", 2)[0];
        Command command = getCommand(cmd);
        if (command != null) {
            if (event.isFromGuild()) {
                logger.info(event.getGuild().getName() + ": " + event.getChannel().getName() + ": " + event.getAuthor().getAsTag()
                        + ": " + event.getMessage().getContentRaw());
            } else {
                logger.info(event.getAuthor().getAsTag() + ": " + event.getMessage().getContentRaw());
            }

            command.execute(new MessageCommandEvent(event, command));
        }
    }

    private void counter(MessageReceivedEvent event) {
        if (event.getChannel().getId().equals("898271566880727130") && !event.getJDA().getSelfUser().getId().equals(event.getAuthor().getId())) {
            try {
                double i = evaluate(event.getMessage().getContentRaw());
                if (Double.isNaN(i))
                    return;
                event.getChannel().sendMessage(i + 1 + "").queue();
            } catch (Exception ignored) {
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
                        // Catches all throwables instead of only exceptions to include stackoverflow and other errors.
                        // They would not crash the bot if not caught, but this makes sure the user is notified that their number will no longer be calculated
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
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

    private double evaluate(String text) {
        try {
            Expression e = new Expression(text);
            return e.calculate();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Command command = getCommand(event.getName());
        if (command != null) {
            logger.info((event.getGuild() != null ? event.getGuild().getName() + ": " + event.getChannel().getName() : event.getChannel().getName())
                    + ": " + event.getUser().getAsTag()
                    + ": " + event.getCommandString());
            command.execute(new SlashCommandCommandEvent(event, command));
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
                    .setDescription(event.getMember().getAsMention() + " Bitte ändere deinen Nickname auf dem Server zu deinem echten Namen: Das macht die Kommunikation etwas leichter.")
                    .setColor(Util.getRandomColor());

            channel.sendMessageEmbeds(builder.build()).queue();
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        String emote = event.getReaction().getEmoji().getAsReactionCode();

        boolean upvote = Config.getInstance().isUpvote(emote);
        boolean downVote = Config.getInstance().isDownvote(emote);
        boolean sus = Config.getInstance().isSus(emote);

        if (upvote || downVote) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            Util.addKarma(message.getAuthor(), upvote ? 1 : -1);
        }

        if (sus) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            Util.addSusCount(message.getAuthor(), 1);
        }

    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        String emote = event.getReaction().getEmoji().getAsReactionCode();

        boolean upvote = Config.getInstance().isUpvote(emote);
        boolean downVote = Config.getInstance().isDownvote(emote);
        boolean sus = Config.getInstance().isSus(emote);

        if (upvote || downVote) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            Util.addKarma(message.getAuthor(), upvote ? -1 : 1);//removing upvotes => remove karma
        }

        if (sus) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            Util.addSusCount(message.getAuthor(), -1);
        }
    }

    public void registerSelectMenuListener(String id, SelectMenuListener listener) {
        selectMenuListeners.put(id, listener);
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        SelectMenuListener listener = selectMenuListeners.get(event.getComponentId());
        if (listener != null) {
            boolean remove = listener.onSelectMenuInteraction(event);
            if (remove)
                selectMenuListeners.remove(event.getComponentId());
        }
    }

    public void registerButtonListener(String id, ButtonListener listener) {
        buttonListeners.put(id, listener);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        ButtonListener listener = buttonListeners.get(event.getComponentId());
        if (listener != null) {
            boolean remove = listener.onButtonInteraction(event);
            if (remove)
                buttonListeners.remove(event.getComponentId());
        }
    }

    public void registerModalListener(String id, ModalListener listener) {
        modalListeners.put(id, listener);
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        ModalListener listener = modalListeners.get(event.getModalId());
        if (listener != null) {
            boolean remove = listener.onModalInteraction(event);
            if (remove)
                selectMenuListeners.remove(event.getModalId());
        }
    }

    private void registerAllSlashCommands() {
        CommandListUpdateAction action = getBot().getJda().updateCommands();
        for (Command command : allCommands) {
            CommandData commandData = command.getCommandData();
            if (!command.isHidden() && commandData != null)
                action = action.addCommands(commandData);
        }
        action.queue();
    }

    /**
     * this is used to automatically register slash commands to a test server (faster than global registration)
     */
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
