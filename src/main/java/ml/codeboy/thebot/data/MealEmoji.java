package ml.codeboy.thebot.data;

import java.util.Collection;

public class MealEmoji {
    private final String emoji;
    private final Collection<String> names;
    private final int priority = 0;

    public MealEmoji(String emoji, Collection<String> names) {
        this.emoji = emoji;
        this.names = names;
    }

    public String getEmoji() {
        return emoji;
    }

    public Collection<String> getNames() {
        return names;
    }
}
