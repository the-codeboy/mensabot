package com.the_codeboy.mensabot.quotes;

import net.dv8tion.jda.api.EmbedBuilder;

public class Quote {
    private String content;
    private long time;
    private transient String person;
    private String authorId;

    public Quote(String content, long time, String person, String authorId) {
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
