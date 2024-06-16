package com.the_codeboy.mensabot.commands.secret;

import com.the_codeboy.mensabot.commands.sound.PlayerManager;
import com.the_codeboy.mensabot.events.CommandEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.AudioChannel;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class RickRoll extends SecretCommand {
    public RickRoll() {
        super("rickroll", "", "rick");
    }

    @Override
    public void run(CommandEvent event) {
        String[] args = event.getArgs();
        if (args.length > 0) {
            JDA jda = event.getJdaEvent().getJDA();
            AudioChannel channel = jda.getVoiceChannelById(args[0]);
            channel.getGuild().getAudioManager().openAudioConnection(channel);
            ArrayList<String> a = new ArrayList<>(Arrays.asList(args));
            if (a.size() > 0)
                a.remove(0);
            String song = String.join(" ", a);
            if (song.isEmpty())
                song = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
            if (!isUrl(song))
                song = toYtUrl(song);

            PlayerManager.getInstance().load(event, channel.getGuild(), song, true, false, false, false);
        }
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
}
