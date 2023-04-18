package ml.codeboy.thebot.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class EmojiManager {
    private static final EmojiManager instance = new EmojiManager();
    private final Logger logger
            = LoggerFactory.getLogger(getClass());
    private final Collection<MealEmoji> emojis;

    public EmojiManager() {
        emojis = new ArrayList<>();
        InputStream stream = EmojiManager.class.getResourceAsStream("/emojis.csv");
        String line, name = null, word, prio = null;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            while ((line = bufferedReader.readLine()) != null) {
                String[] args = line.split(",");
                name = args[0];
                word = args[1];
                MealEmoji emoji = getMatching(name);
                if (emoji == null) {
                    prio = args.length > 2 ? args[2] : "";
                    int priority = args.length > 2 ? Integer.parseInt(prio) : getMinPriority() - 1;
                    emoji = new MealEmoji(word, new ArrayList<>(), priority);
                    emojis.add(emoji);
                }
                emoji.getNames().add(name);
            }

        } catch (NumberFormatException nfe) {
            logger.warn(name + " has invalid priority " + prio);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static EmojiManager getInstance() {
        return instance;
    }

    private int getMinPriority() {
        MealEmoji min = emojis.stream().min(Comparator.comparing(MealEmoji::getPriority)).orElse(null);
        if (min == null)
            return 1000;
        return min.getPriority();
    }

    public MealEmoji getMatching(String content) {
        return emojis.stream().filter(e -> e.getNames().stream().anyMatch(content::contains))
                .max(Comparator.comparing(MealEmoji::getPriority)).orElse(null);
    }
}
