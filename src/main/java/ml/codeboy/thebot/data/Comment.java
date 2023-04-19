package ml.codeboy.thebot.data;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class Comment {
    @BsonProperty
    private String content;
    @BsonProperty
    private String meal;

    public Comment(String content, String meal) {
        this.content = content;
        this.meal = meal;
    }

    public Comment(){}

    public String getContent() {
        return content;
    }

    public String getMeal() {
        return meal;
    }

    public void setContent(String c)
    {
        this.content = c;
    }

    public void setMeal(String m)
    {
        this.meal = m;
    }
}
