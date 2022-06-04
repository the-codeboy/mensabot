package ml.codeboy.thebot.apis;

import java.util.Arrays;

public class Meme {
    private String postLink;
    private String subreddit;
    private String title;
    private String url;
    private String author;
    private int ups;
    private String[] preview;

    public String getPostLink() {
        return postLink;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getAuthor() {
        return author;
    }

    public int getUps() {
        return ups;
    }

    public String[] getPreview() {
        return preview;
    }

    @Override
    public String toString() {
        return "Meme{" +
                "postLink='" + postLink + '\'' +
                ", subreddit='" + subreddit + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", author='" + author + '\'' +
                ", ups=" + ups +
                ", preview=" + Arrays.toString(preview) +
                '}';
    }
}
