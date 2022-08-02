package ml.codeboy.thebot;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;

public interface SelectMenuListener {
    void onSelectMenuInteraction(SelectMenuInteractionEvent event);
}
