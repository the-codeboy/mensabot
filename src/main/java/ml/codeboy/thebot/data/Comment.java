package ml.codeboy.thebot.data;

public class Comment {
    private final String content, meal;

    public Comment(String content, String meal) {
        this.content = content;
        this.meal = meal;
    }

    public String getContent() {
        return content;
    }

    public String getMeal() {
        return meal;
    }
}
