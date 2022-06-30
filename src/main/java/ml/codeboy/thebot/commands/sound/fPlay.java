package ml.codeboy.thebot.commands.sound;

import ml.codeboy.thebot.events.CommandEvent;

public class fPlay extends AudioCommand {
    public fPlay() {
        super("fplay", "Play the given song and then resume with the playlist");
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
                }
            }
            event.getManager().audioPlayer.setPaused(false);
            return;
        }

        if (event.getManager().audioPlayer.getPlayingTrack() != null) {
            String n = event.getManager().audioPlayer.getPlayingTrack().getInfo().uri+"&t="+(int)(event.getManager().audioPlayer.getPlayingTrack().getPosition()/1000);
            queue(event, n, true);
        }

        String link = String.join(" ", event.getArgs());
        play(event, link);
    }

}