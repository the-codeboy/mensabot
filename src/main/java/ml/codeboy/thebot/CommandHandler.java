package ml.codeboy.thebot;

import com.github.codeboy.api.Mensa;
import com.github.codeboy.jokes4j.Jokes4J;
import com.github.codeboy.jokes4j.api.Flag;
import com.github.codeboy.jokes4j.api.JokeRequest;
import com.github.codeboy.piston4j.api.Piston;
import ml.codeboy.met.Weather4J;
import ml.codeboy.met.data.Forecast;
import ml.codeboy.thebot.apis.AdviceApi;
import ml.codeboy.thebot.commands.*;
import ml.codeboy.thebot.commands.debug.GetQuotes;
import ml.codeboy.thebot.commands.debug.ListQuotes;
import ml.codeboy.thebot.commands.image.MorbCommand;
import ml.codeboy.thebot.commands.image.ShitCommand;
import ml.codeboy.thebot.commands.image.meme.*;
import ml.codeboy.thebot.commands.mensa.*;
import ml.codeboy.thebot.commands.quotes.AddQuote;
import ml.codeboy.thebot.commands.quotes.AddQuoteList;
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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ml.codeboy.thebot.WeatherUtil.generateForecastImage;

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


    public void registerKnowCommands() {
        this.registerCommand(new Help(bot));

        registerCommand(new ChuckNorrisJokeCommand());
//        registerCommand(new JermaCommand());
        registerCommand(new TrumpQuoteCommand());
        registerCommand(new AdviceCommand());
        registerCommand(new NewsCommand());
        registerCommand(new InsultCommand());
        registerCommand(new RhymeCommand());
        registerCommand(new MemeCommand());
        registerCommand(new JokeCommand());
        registerCommand(new ShortsCommand());
        registerCommand(new WeatherCommand());
        registerCommand(new PingCommand());
        registerCommand(new ShittyTranslateCommand());
        registerCommand(new ASCIICommand());

        registerCommand(new MensaCommand());
        registerCommand(new RateCommand());
        registerCommand(new DefaultMensaCommand());
        registerCommand(new MensaAnnounceChannelCommand());
        registerCommand(new DetailCommand());
        registerCommand(new AddImageCommand());

        registerCommand(new DönerrateCommand());
        registerCommand(new Dönertop());

        registerCommand(new ExecuteCommand());
        registerCommand(new LanguagesCommand());

        registerAudioCommands();

        registerCommand(new AddQuote());
        registerCommand(new AddQuoteList());
//        registerCommand(new QuoteCommand());
        registerCommand(new Karma());
        registerCommand(new KarmaTop());
        registerCommand(new KarmaBottom());

        registerImageCommands();

        registerSecretCommands();

        registerDebugCommands();

        registerAllSlashCommands();

        if (Config.getInstance().quoteStatus) {
            changeStatus();
        }
    }

    private void registerImageCommands() {
        registerCommand(new MorbCommand());
        registerCommand(new ShitCommand());
        registerCommand(new ChangeMyMindCommand());
        registerCommand(new HotlineBlingCommand());
        registerCommand(new TwoButtonsCommand());
        registerCommand(new Draw25Command());
        registerCommand(new DisasterGirlCommand());
        registerCommand(new SupermanCommand());
    }

    private void registerSecretCommands() {
        registerCommand(new RickRoll());
        registerCommand(new React());
        registerCommand(new Msg());
        registerCommand(new LoadKarma());
        registerCommand(new Bee());
        registerCommand(new AcceptImage());
        registerCommand(new RejectImage());
    }

    private void registerDebugCommands() {
        registerCommand(new ListQuotes());
        registerCommand(new GetQuotes());
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
        registerCommand(new fPlay());
        registerCommand(new Pause());
        registerCommand(new Resume());
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
            event.getMessage().addReaction(amogus).queue();
        }
        if (msg.contains("sus")) {
            logger.info("sus");
            event.getMessage().addReaction(amogus).queue();
            event.getMessage().addReaction(sus).queue();
        }

        if (event.getAuthor().getId().equals("290368310711681024") && !event.getChannel().getId().equals("917201826271604736")) {
            event.getMessage().addReaction(downvote).queue();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        amogus(event);
        counter(event);
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

            command.execute(new MessageCommandEvent(event));
        }
    }

    private void counter(MessageReceivedEvent event) {
        if (event.getChannel().getId().equals("898271566880727130") && !event.getJDA().getSelfUser().getId().equals(event.getAuthor().getId())) {
            try {
                float i = evaluate(event.getMessage().getContentRaw());
                event.getChannel().sendMessage(i + 1 + "").queue();
            } catch (Exception ignored) {
            }
        }
    }

    private float evaluate(String text) {
        try {
            float value = Float.parseFloat(text);
            return value;
        } catch (NumberFormatException ignored) {//assume this is an expression that needs to be evaluated first
        }
        text = text.replace("pi", "math.pi");
        text = text.replace("e", "math.e");
        text = text.replace("sqrt", "math.sqrt");
        text = text.replace("abs", "math.abs");
        text = text.replace("g", "9.81");
        text = text.replace("R", "8.314");
        logger.info(text);
        String result = Piston.getDefaultApi().execute("python", "import math\nprint(" + text + ",end=\"\")").getOutput().getOutput();
        logger.info("\"" + result + "\"");
        return Float.parseFloat(result);
    }


    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Command command = getCommand(event.getName());
        if (command != null) {
            logger.info((event.getGuild() != null ? event.getGuild().getName() + ": " + event.getChannel().getName() : event.getChannel().getName())
                    + ": " + event.getUser().getAsTag()
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

        if (upvote || downVote) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            Util.addKarma(message.getAuthor(), upvote ? 1 : -1);
        }

    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        String emote = event.getReaction().getEmoji().getAsReactionCode();

        boolean upvote = Config.getInstance().isUpvote(emote);
        boolean downVote = Config.getInstance().isDownvote(emote);

        if (upvote || downVote) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            Util.addKarma(message.getAuthor(), upvote ? -1 : 1);//removing upvotes => remove karma
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
                selectMenuListeners.remove(event.getComponentId());
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
