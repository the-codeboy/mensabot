package ml.codeboy.thebot.quotes;

import net.dv8tion.jda.api.EmbedBuilder;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class Quote {
    @BsonProperty("content")
    private String content;
    @BsonProperty("time")
    private long time;
    @BsonProperty("name")
    private String person;
    @BsonProperty("authorId")
    private String authorId;

    @BsonCreator
    public Quote(@BsonProperty("content") String content,
                 @BsonProperty("time") long time,
                 @BsonProperty("name") String person,
                 @BsonProperty("authorId")String authorId) {
        this.content = content;
        this.time = time;
        this.person = person;
        this.authorId = authorId;
    }

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
        if (getContent().length() <= 256)
            return new EmbedBuilder().setTitle(getContent()).setDescription("||" + getPerson() + "||");
        EmbedBuilder builder = new EmbedBuilder();
        builder.addField("||" + getPerson() + "||", "", true);
        String content = getContent();
        while (content.length() > 1024) {
            builder.addField("", content.substring(0, 1024), false);
            content = content.substring(1024);
        }
        builder.addField("", content, false);
        return builder;
    }
}
