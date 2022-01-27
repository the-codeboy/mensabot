package ml.codeboy.thebot.commands.sound;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.*;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final ArrayList<AudioTrack> queue=new ArrayList<>();
    private ArrayList<AudioTrack>nextQueue=null;
    private CommandEvent latestEvent=null;
    private int loop=0,currentLoop=0;
    private boolean destroyed=false;
    private GuildMusicManager manager;

    public TrackScheduler(GuildMusicManager manager) {
        this.player = manager.audioPlayer;
        this.manager=manager;
    }

    public void queue(AudioTrack track) {
        queue(track,false);
    }
    public void queue(AudioTrack track,boolean playNext) {
        if (!this.player.startTrack(track, true)) {
            if(playNext)
                this.queue.add(0,track);
            else
                this.queue.add(track);
        }
    }

    public void playNext(AudioTrack track) {
        if (!this.player.startTrack(track, true)) {
            this.queue.add(0,track);
        }
    }

    public void nextTrack() {
        if(destroyed)
            return;

        AudioTrack track=queue.size()>0?this.queue.remove(0):null;

        if(track==null&&nextQueue!=null){
            for (AudioTrack t:nextQueue)
                queue.add(t.makeClone());
            track=queue.size()>0?this.queue.remove(0):null;

        }

        if(track==null){
            if(latestEvent!=null)
                latestEvent.replyError("There are no more tracks in the queue");
            return;
        }
        play(track);
        currentLoop=0;
    }

    public void play(AudioTrack track){
        this.player.startTrack(track, false);
        if(latestEvent!=null){
            Util.sendTrackInfo(latestEvent,track);
        }
    }

    public void loop(int times){
        loop=times;
//        System.out.println("looping "+times+" times");
    }

    public void loopQueue(){
        nextQueue=new ArrayList<>(queue);
    }

    public void dontLoopQueue(){
        nextQueue=null;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason==AudioTrackEndReason.FINISHED&&currentLoop++<loop){
            System.out.println("looping");
            play(track.makeClone());
        }else if (endReason!=AudioTrackEndReason.REPLACED) {
            if(endReason.mayStartNext||nextQueue!=null)
                nextTrack();
            else if(latestEvent!=null)
                PlayerManager.getInstance().destroy(latestEvent.getGuild());
        }
    }

    public void sendQueueInfo(CommandEvent event){
        EmbedBuilder builder=event.getBuilder();
        Util.sign(builder,event).setTitle("Current queue");
        if(queue.isEmpty())
            builder.setDescription("empty");
        else
            for (int i = 0; i < queue.size(); i++) {
                if(i>5)
                    break;
                AudioTrack track=queue.get(i);
                builder.addField((i+1)+". "+track.getInfo().title,
                        "by " + track.getInfo().author,
                        true);
            }
        event.reply(builder);
    }

    public void setLatestEvent(CommandEvent latestEvent) {
        this.latestEvent = latestEvent;
    }

    public void shuffle(){
        List<AudioTrack>tracks= Arrays.asList(queue.toArray(new AudioTrack[0]));
        Collections.shuffle(tracks);
        queue.clear();
        queue.addAll(tracks);
    }

    public int songsInQueue(){
        return queue.size();
    }

    public boolean removeTrack(int id){
        Iterator<AudioTrack> iterator = queue.iterator();
        int i=1;
        while(iterator.hasNext())
        {
            AudioTrack track= iterator.next();
            if(i++==id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }


    public void destroy(){
        destroyed=true;
    }
}