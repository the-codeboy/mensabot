package ml.codeboy.thebot;

import ml.codeboy.thebot.util.ButtonListener;
import ml.codeboy.thebot.util.ModalListener;
import ml.codeboy.thebot.util.SelectMenuListener;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.regex.Pattern;

public class InteractionHandler extends ListenerAdapter {

    private static final InteractionHandler instance = new InteractionHandler();
    private final HashMap<String, SelectMenuListener> selectMenuListeners = new HashMap<>();
    private final HashMap<Pattern, SelectMenuListener> regexSelectMenuListeners = new HashMap<>();
    private final HashMap<String, ButtonListener> buttonListeners = new HashMap<>();
    private final HashMap<Pattern, ButtonListener> regexButtonListeners = new HashMap<>();
    private final HashMap<String, ModalListener> modalListeners = new HashMap<>();
    private final HashMap<Pattern, ModalListener> regexModalListeners = new HashMap<>();

    public static InteractionHandler getInstance() {
        return instance;
    }

    public void registerSelectMenuListener(String id, SelectMenuListener listener) {
        selectMenuListeners.put(id, listener);
    }

    /**
     * @param listener the listener
     * @param regex    a regular expression matching the button ids to listen for
     */
    public void registerRegexSelectMenuListener(SelectMenuListener listener, String regex) {
        Pattern pattern = Pattern.compile(regex);
        regexSelectMenuListeners.put(pattern, listener);
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        SelectMenuListener listener = selectMenuListeners.get(event.getComponentId());
        for (Pattern pattern : regexSelectMenuListeners.keySet()) {
            if (listener == null && pattern.matcher(event.getComponentId()).matches()) {
                listener = regexSelectMenuListeners.get(pattern);
            }
        }
        if (listener != null) {
            boolean remove = listener.onSelectMenuInteraction(event);
            if (remove)
                selectMenuListeners.remove(event.getComponentId());
        }
    }

    public void registerButtonListener(String id, ButtonListener listener) {
        buttonListeners.put(id, listener);
    }

    /**
     * @param listener the listener
     * @param regex    a regular expression matching the button ids to listen for
     */
    public void registerRegexButtonListener(ButtonListener listener, String regex) {
        Pattern pattern = Pattern.compile(regex);
        regexButtonListeners.put(pattern, listener);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        ButtonListener listener = buttonListeners.get(event.getComponentId());
        for (Pattern pattern : regexButtonListeners.keySet()) {
            if (listener == null && pattern.matcher(event.getComponentId()).matches()) {
                listener = regexButtonListeners.get(pattern);
            }
        }
        if (listener != null) {
            boolean remove = listener.onButtonInteraction(event);
            if (remove)
                buttonListeners.remove(event.getComponentId());
        }
    }

    public void registerModalListener(String id, ModalListener listener) {
        modalListeners.put(id, listener);
    }

    /**
     * @param listener the listener
     * @param regex    a regular expression matching the button ids to listen for
     */
    public void registerRegexModalListener(ModalListener listener, String regex) {
        Pattern pattern = Pattern.compile(regex);
        regexModalListeners.put(pattern, listener);
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        ModalListener listener = modalListeners.get(event.getModalId());
        for (Pattern pattern : regexModalListeners.keySet()) {
            if (listener == null && pattern.matcher(event.getModalId()).matches()) {
                listener = regexModalListeners.get(pattern);
            }
        }
        if (listener != null) {
            boolean remove = listener.onModalInteraction(event);
            if (remove)
                selectMenuListeners.remove(event.getModalId());
        }
    }

}
