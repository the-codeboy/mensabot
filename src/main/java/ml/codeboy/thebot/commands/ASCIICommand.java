package ml.codeboy.thebot.commands;

import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.util.ASCII;
import ml.codeboy.thebot.util.Util;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ASCIICommand extends Command {
    public ASCIICommand() {
        super("ascii", "converts and image to ascii");
        setHidden(true);
    }

    @Override
    public SlashCommandData getCommandData() {
        return null;//make sure not slash command gets registered
    }

    @Override
    public void run(CommandEvent event) {
        if (event.isSlashCommandEvent()) {
            getLogger().warn("command was run as slash command which should not be possible");
            return;//this should never happen
        }
        Message message = event.getMessageReceivedEvent().getMessage();
        String url;
        if (message.getAttachments().isEmpty()) {
            String[] args = event.getArgs();
            if (args.length == 0) {
                event.replyError("please attach an image or supply an url");
                return;
            }
            url = args[0];
        } else {
            Message.Attachment attachment = message.getAttachments().get(0);
            url = attachment.getUrl();
        }
        try {
            BufferedImage image = Util.getImageFromUrl(url);
            String ascii = ASCII.convert(ASCII.scale(image));
            if (ascii.length() < 2000)
                event.reply("\n```" + ascii + "```");
            else event.replyError("too much text " + ascii.length());
        } catch (IOException e) {
            event.replyError("unable to load image from url");
        }
    }
}
