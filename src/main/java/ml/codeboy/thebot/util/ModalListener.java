package ml.codeboy.thebot.util;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public interface ModalListener {
    /**
     * @param event the event
     * @return true when the listener should be unregistered after this event
     */
    boolean onModalInteraction(ModalInteractionEvent event);
}
