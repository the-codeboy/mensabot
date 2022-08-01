package ml.codeboy.thebot.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

public class EmojiManager {
    private static final EmojiManager instance = new EmojiManager();
    private final Collection<MealEmoji> emojis;

    public EmojiManager() {
        emojis = new ArrayList<>();
        InputStream stream = EmojiManager.class.getResourceAsStream("/emojis.csv");
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] args = line.split(",");
                String name = args[0], word = args[1];
                MealEmoji emoji = getMatching(name);
                if (emoji == null) {
                    emoji = new MealEmoji(word, new ArrayList<>());
                    emojis.add(emoji);
                }
                emoji.getNames().add(name);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static EmojiManager getInstance() {
        return instance;
    }

    public MealEmoji getMatching(String content) {
        return emojis.stream().filter(e -> e.getNames().stream().anyMatch(content::contains))
                .findFirst().orElse(null);
    }
}
