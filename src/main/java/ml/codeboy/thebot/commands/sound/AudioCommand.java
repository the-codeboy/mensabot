package ml.codeboy.thebot.commands.sound;

import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AudioCommand extends Command {
    public AudioCommand(String name) {
        this(name, "");
    }

    public AudioCommand(String name, String usage, String... aliases) {
        super(name, usage, aliases);
        setHidden(true);
    }

    @Override
    public void run(CommandEvent event) {

    }

    @Override
    public void execute(CommandEvent event) {
        event.setEphermal(true);
        super.execute(event);
//        event.getManager().scheduler.setLatestEvent(event);//song info will now only be send if current is run
    }

    /**
     * Ensures that the user is in a voice channel
     * @param event
     * @return true if the bot is connected, false if the bot could not join
     */
    protected boolean ensureConnected(CommandEvent event) {
        final Member self = event.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();


        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (memberVoiceState.getChannel() == null) {
            event.reply("You need to be in a voice channel for this command to work");
            return false;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();

        if (selfVoiceState.getChannel() == null) {
            audioManager.openAudioConnection(memberVoiceState.getChannel());
            return true;
        }

        if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            event.reply("You need to be in the same voice channel as me for this to work");
            return false;
        }
        //already connected
        return true;
    }
    
    protected boolean ensureSameChannel(CommandEvent event)
    {
        final Member self = event.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();
        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();
        if (memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
            return true;
        }
        event.reply("You need to be in the same voice channel as me for this to work");
        return false;
    }

    protected void queue(CommandEvent event, String link) {
        queue(event, link, false);
    }

    protected void queue(CommandEvent event, String link, boolean playNext) {
        link = toYtUrl(link);

        PlayerManager.getInstance().load(event, link, false, playNext);
    }

    protected void play(CommandEvent event, String link) {
        play(event, link, false);
    }

    protected void play(CommandEvent event, String link, boolean shuffle) {
        link = toYtUrl(link);

        PlayerManager.getInstance().load(event, link, true, true, false, shuffle);
    }

    private String toYtUrl(String link) {
        if (!isUrl(link)) {
            File file = new File("music" + File.separator + link + ".mp3");
            if (file.exists())//music available offline
            {
                return "file://" + file.getPath();
            }
            link = "ytsearch:" + link;
        }
        return link;
    }

    private boolean isUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    protected void shuffle(CommandEvent event) {
        event.getManager().scheduler.shuffle();
    }

}
