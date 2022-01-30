package ml.codeboy.thebot.quotes;

import net.dv8tion.jda.api.EmbedBuilder;

public class Quote {
    private String content;
    private long time;
    private transient String person;
    private String authorId;

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public EmbedBuilder builder() {
        return new EmbedBuilder().setTitle(getContent()).setDescription("||" + getPerson() + "||");
    }
}
