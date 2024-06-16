package com.the_codeboy.mensabot.apis;

import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;

public class NewsArticle {
    private String author, content, imageUrl, readMoreUrl, title;

    public EmbedBuilder createEmbed(EmbedBuilder builder) {
        builder.setAuthor(author);
        builder.setTitle(title, readMoreUrl);
        builder.setThumbnail(imageUrl);
        builder.setDescription(content);
        return builder;
    }

    public static class NewsArticleResponse {
        public ArrayList<NewsArticle> data;
    }
}
