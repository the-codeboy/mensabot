package ml.codeboy.thebot.commands.sound;


import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.util.Util;

public class Volume extends AudioCommand{
    public Volume() {
        super("volume");
    }

    @Override
    public void run(CommandEvent event) {
        if(event.getArgs().length==0) {
            event.reply(event.getBuilder().setTitle("Volume").setDescription(event.getManager().audioPlayer.getVolume()/10 + "%"));
            return;
        }
        String arg1=event.getArgs()[0];
        if(Util.isInt(arg1)){
            int volume=Util.toInt(arg1);
            if(volume>-1&&volume<=100){
                event.getManager().audioPlayer.setVolume(10*volume);
            }
        }else if(arg1.equalsIgnoreCase("up")){
            event.getManager().audioPlayer.setVolume(10+event.getManager().audioPlayer.getVolume());
        }else if(arg1.equalsIgnoreCase("down")){
            event.getManager().audioPlayer.setVolume(-10+event.getManager().audioPlayer.getVolume());
        }
    }
}
