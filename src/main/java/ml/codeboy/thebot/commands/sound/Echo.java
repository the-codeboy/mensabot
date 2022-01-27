package ml.codeboy.thebot.commands.sound;

import ml.codeboy.thebot.commands.Command;
import ml.codeboy.thebot.events.CommandEvent;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Echo extends Command {
    public Echo(){
        super("echo","makes the bot repeat everything you say");
    }


    @Override
    public void run(CommandEvent event) {
        AudioChannel channel=event.getMember().getVoiceState()==null?
                event.getGuild().getAfkChannel():event.getMember().getVoiceState().getChannel();
        if(channel==null){
            event.reply("please join a voice channel and run the command again");
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();

        EchoHandler handler=new EchoHandler();

        audioManager.setSendingHandler(handler);
        audioManager.setReceivingHandler(handler);
        audioManager.openAudioConnection(channel);
    }

    public static class EchoHandler implements AudioSendHandler, AudioReceiveHandler
    {
        private final Queue<byte[]> queue = new ConcurrentLinkedQueue<>();


        @Override
        public boolean canReceiveCombined()
        {
            return queue.size() < 10;
        }

        @Override
        public void handleCombinedAudio(CombinedAudio combinedAudio)
        {
            if (combinedAudio.getUsers().isEmpty())
                return;

            byte[] data = combinedAudio.getAudioData(1.0f);
            queue.add(data);
        }

        @Override
        public boolean canProvide()
        {
            return !queue.isEmpty();
        }

        @Override
        public ByteBuffer provide20MsAudio()
        {
            byte[] data = queue.poll();
            return data == null ? null : ByteBuffer.wrap(data);
        }

        @Override
        public boolean isOpus()
        {
            return false;
        }
    }
}


