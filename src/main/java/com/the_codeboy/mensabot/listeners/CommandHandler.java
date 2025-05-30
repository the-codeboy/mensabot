package com.the_codeboy.mensabot.listeners;

import com.the_codeboy.mensabot.Bot;
import com.the_codeboy.mensabot.Config;
import com.the_codeboy.mensabot.commands.*;
import com.the_codeboy.mensabot.commands.debug.GetQuotes;
import com.the_codeboy.mensabot.commands.debug.ListQuotes;
import com.the_codeboy.mensabot.commands.image.WeatherCommand;
import com.the_codeboy.mensabot.commands.leaderboard.LeaderBoard;
import com.the_codeboy.mensabot.commands.mensa.*;
import com.the_codeboy.mensabot.commands.nils.ElMomentoCommand;
import com.the_codeboy.mensabot.commands.quotes.AddQuote;
import com.the_codeboy.mensabot.commands.quotes.QuoteCommand;
import com.the_codeboy.mensabot.commands.secret.*;
import com.the_codeboy.mensabot.events.MessageCommandEvent;
import com.the_codeboy.mensabot.events.SlashCommandCommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CommandHandler extends ListenerAdapter {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Bot bot;
    private final HashMap<String, Command> commands = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private Guild server;

    public CommandHandler(Bot bot) {
        this.bot = bot;
        String serverID = Config.getInstance().serverId;
        try {
            bot.getJda().awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (serverID != null)
            server = bot.getJda().getGuildById(serverID);

        this.registerKnownCommands();
    }

    private void createCommand(Class<? extends Command> command) {
        executor.execute(() -> {
            try {
                registerCommand(command.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("failed to create Command " + command.getName(), e);
            }
        });
    }

    public void registerKnownCommands() {
        this.registerCommand(new Help(bot));

        createCommand(ChuckNorrisJokeCommand.class);
        createCommand(JermaCommand.class);
        createCommand(AdviceCommand.class);
        createCommand(InsultCommand.class);
        createCommand(MemeCommand.class);
        createCommand(JokeCommand.class);
        createCommand(ShortsCommand.class);
        createCommand(WeatherCommand.class);
        createCommand(GifCommand.class);
        createCommand(XKCDCommand.class);

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

        createCommand(AddQuote.class);
        createCommand(QuoteCommand.class);

        registerLeaderBoardCommands();

        registerImageCommands();

        registerSecretCommands();

        registerNilsCommands();

        registerDebugCommands();

        try {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.error("bot did not start in time", e);
        }

        registerAllSlashCommands();
    }

    private void registerImageCommands() {
        createCommand(LatexCommand.class);
    }

    private void registerSecretCommands() {
        createCommand(React.class);
        createCommand(Msg.class);
        createCommand(LoadKarma.class);
        createCommand(LoadSusCount.class);
        createCommand(Bee.class);
        createCommand(AcceptImage.class);
        createCommand(RejectImage.class);
        createCommand(AnnounceCommand.class);
    }

    private void registerNilsCommands() {
        createCommand(ElMomentoCommand.class);
    }

    private void registerDebugCommands() {
        createCommand(ListQuotes.class);
        createCommand(GetQuotes.class);
    }

    private void registerLeaderBoardCommands() {
        LeaderBoard.registerAll(this);
    }

    public void registerCommand(Command command) {
        command.register(this);
        commands.put(command.getName().toLowerCase(), command);
        for (String alias : command.getAliases()) {
            commands.put(alias.toLowerCase(), command);
        }
        CommandData data = command.getCommandData();
        if (data != null && !command.isHidden())
            registerSlashCommand(data);
        logger.info("registered command " + command.getName());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        if (!event.isFromGuild() && !event.getAuthor().isBot()) {
            TextChannel channel = (TextChannel) getBot().getJda()
                    .getGuildChannelById(Config.getInstance().dmDebugChannel);
            if (channel != null) {
                if (event.getMessage().getContentRaw().length() > 0)
                    channel.sendMessageEmbeds(new EmbedBuilder()
                            .setAuthor(event.getAuthor().getAsTag() + " " + event.getAuthor().getAsMention())
                            .setDescription(content).setThumbnail(event.getAuthor().getAvatarUrl())
                            .setTimestamp(event.getMessage().getTimeCreated()).build()).queue();
                else {
                    for (Message.Attachment attachment : event.getMessage().getAttachments()) {
                        try {
                            channel.sendMessageEmbeds(new EmbedBuilder()
                                    .setAuthor(event.getAuthor().getAsTag() + " " + event.getAuthor().getAsMention())
                                    .setThumbnail(event.getAuthor().getAvatarUrl())
                                    .setTimestamp(event.getMessage().getTimeCreated())
                                    .setDescription(attachment.getDescription() + "").build()).queue();
                            channel.sendFile(attachment.getProxy().download().get(), attachment.getFileName())
                                    .complete();
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
                logger.info(event.getGuild().getName() + ": " + event.getChannel().getName() + ": "
                        + event.getAuthor().getAsTag()
                        + ": " + event.getMessage().getContentRaw());
            } else {
                logger.info(event.getAuthor().getAsTag() + ": " + event.getMessage().getContentRaw());
            }

            command.execute(new MessageCommandEvent(event, command));
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Command command = getCommand(event.getName());
        if (command != null) {
            logger.info((event.getGuild() != null ? event.getGuild().getName() + ": " + event.getChannel().getName()
                    : event.getChannel().getName())
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

    private void registerAllSlashCommands() {
        CommandListUpdateAction action = getBot().getJda().updateCommands();
        for (Command command : getCommands()) {
            CommandData commandData = command.getCommandData();
            if (!command.isHidden() && commandData != null)
                action = action.addCommands(commandData);
        }
        action.queue();
    }

    /**
     * this is used to automatically register slash commands to a test server
     * (faster than global registration)
     */
    private void registerSlashCommand(CommandData data) {
        if (getServer() != null)
            getServer().upsertCommand(data).queue();
    }

    private Bot getBot() {
        return bot;
    }

    public Command getCommand(String name) {
        return commands.get(name.toLowerCase());
    }

    public Collection<Command> getCommands() {
        return new HashSet<>(commands.values());
    }

    public Guild getServer() {
        return server;
    }
}
