package ml.codeboy.thebot.data;

import java.util.Collection;

public class MealEmoji {
    private final String emoji;
    private final Collection<String> names;
    private final int priority;

    public MealEmoji(String emoji, Collection<String> names, int priority) {
        this.emoji = emoji;
        this.names = names;
        this.priority = priority;
    }

    public String getEmoji() {
        return emoji;
    }

    public Collection<String> getNames() {
        return names;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return getEmoji();
    }
}
