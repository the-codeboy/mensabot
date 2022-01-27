package ml.codeboy.thebot.quotes;

public class Quote {
    private String content;
    private long time;
    private transient String person;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
