package ml.codeboy.thebot.data;

import java.util.UUID;

public class MealImage {
    private final String author;
    private final String url;
    private final UUID id = UUID.randomUUID();
    private boolean accepted = false;

    public MealImage(String author, String url) {
        this.author = author;
        this.url = url;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public UUID getId() {
        return id;
    }
}
