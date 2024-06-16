package com.the_codeboy.mensabot;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LogAppender extends AppenderBase<ILoggingEvent> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Collection<TextChannel> debugChannels = new ArrayList<>();
    private final List<ILoggingEvent> loggingQueue = new ArrayList<>();//contains events from before the bot was started
    private boolean initialised = false;

    @Override
    protected void append(ILoggingEvent event) {
        if (event.getLoggerName().startsWith("org.mongodb.driver"))
            return;//do not log messages from mongodb
        if (initialised) {
            log(event);
        } else {
            loggingQueue.add(event);
            attemptInit();
        }
    }

    private synchronized void attemptInit() {
        if (initialised)
            return;
        MensaBot bot = MensaBot.getInstance();
        if (bot == null)
            return;
        JDA jda = bot.getJda();
        if (jda != null && jda.getStatus() == JDA.Status.CONNECTED) {
            for (String channelId : Config.getInstance().debugChannels) {
                try {
                    TextChannel channel = jda.getTextChannelById(channelId);
                    if (channel != null)
                        debugChannels.add(channel);
                } catch (Exception e) {
                    logger.error("Unable to load debug channel with id " + channelId);
                }
            }
            //events from before the bot started are most likely uninteresting
//            for (ILoggingEvent event:loggingQueue){
//                log(event);
//            }
            log(loggingQueue.get(loggingQueue.size() - 1));//only send latest message
            loggingQueue.clear();
            initialised = true;
        }
    }

    private void log(ILoggingEvent event) {
        for (TextChannel channel : debugChannels) {
            channel.sendMessage(format(event)).queue();
        }
    }

    private String format(ILoggingEvent event) {
        return event.getFormattedMessage();
    }
}
