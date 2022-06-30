package ml.codeboy.thebot.commands.sound;

import ml.codeboy.thebot.events.CommandEvent;

public class Play extends AudioCommand {
    public Play() {
        super("fplay");
    }

    @Override
    public void run(CommandEvent event) {

        if (!ensureConnected(event))
            return;

        if (event.getArgs().length == 0) {
            if (event.getManager().audioPlayer.getPlayingTrack() == null) {
                if (event.getManager().scheduler.songsInQueue() == 0) {
                    play(event, "https://www.youtube.com/watch?v=w2Ov5jzm3j8&list=PLurPBtLcqJqcg3r-HOhR3LZ0aDxpI15Fa", true);
                    shuffle(event);
                } else if (event.getManager().audioPlayer.getPlayingTrack() != null) {
                    event.getManager().audioPlayer.playTrack(event.getManager().audioPlayer.getPlayingTrack().makeClone());
                }
            }
            event.getManager().audioPlayer.setPaused(false);
            return;
        }

        String link = String.join(" ", event.getArgs());
        play(event, link);
    }

}