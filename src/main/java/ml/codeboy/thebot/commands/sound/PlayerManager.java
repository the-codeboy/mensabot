package ml.codeboy.thebot.commands.sound;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager, guild);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void load(CommandEvent event, String trackUrl, boolean play, boolean playNext) {
        load(event, trackUrl, play, true, playNext);
    }

    public void load(CommandEvent event, String trackUrl, boolean play, boolean notify, boolean playNext) {
        load(event, trackUrl, play, notify, playNext, false);
    }

    public void load(CommandEvent event, String trackUrl, boolean play, boolean notify, boolean playNext, boolean shuffle) {
        load(event, event.getGuild(), trackUrl, play, notify, playNext, shuffle);
    }

    public void load(CommandEvent event, Guild guild, String trackUrl, boolean play, boolean notify, boolean playNext, boolean shuffle) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (!play) {
                    musicManager.scheduler.queue(track, playNext);

                    if (notify)
                        songAddedToQueue(event, track);
                } else {
                    musicManager.audioPlayer.startTrack(track, false);

                    if (notify)
                        songPlaying(event, track);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.isSearchResult() && playlist.getTracks().size() > 0) {
                    trackLoaded(playlist.getTracks().get(0));
                    return;
                }

                if (shuffle)
                    Collections.shuffle(playlist.getTracks());
                for (AudioTrack track : playlist.getTracks()) {
                    musicManager.scheduler.queue(track);
                }

                if (notify)
                    playlistAddedToQueue(event, playlist);
            }

            @Override
            public void noMatches() {
                if (notify)
                    event.reply("No matches");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                exception.printStackTrace();
                System.err.println("load failed");
                event.replyErrorUnknown();
            }
        });
    }

    public void songAddedToQueue(CommandEvent event, AudioTrack track) {
        event.reply(event.getBuilder().setTitle("Song added to queue")
                .addField(track.getInfo().title, "by `" + track.getInfo().author + '`', true));
    }

    public void songPlaying(CommandEvent event, AudioTrack track) {
        event.reply(event.getBuilder().setTitle("Now playing")
                .addField(track.getInfo().title, " by `" + track.getInfo().author + '`', true));
    }

    public void playlistAddedToQueue(CommandEvent event, AudioPlaylist playlist) {
        event.reply(event.getBuilder().setTitle("Playlist added to queue")
                .addField(playlist.getName(), "with " + playlist.getTracks().size() + " tracks", true));
    }

    public void destroy(Guild guild) {
        GuildMusicManager musicManager = musicManagers.remove(guild.getIdLong());
        if (musicManager != null)
            musicManager.destroy();
        guild.getAudioManager().closeAudioConnection();
    }


}