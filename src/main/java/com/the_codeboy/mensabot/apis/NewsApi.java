package com.the_codeboy.mensabot.apis;

import java.util.ArrayList;

public class NewsApi extends CachedAPI<NewsArticle> {
    private static final NewsApi instance = new NewsApi();

    public static NewsApi getInstance() {
        return instance;
    }

    @Override
    protected NewsArticle requestObject() {
        try {
            String json = readUrl("https://inshortsapi.vercel.app/news?category=all");
            ArrayList<NewsArticle> articles = gson.fromJson(json, NewsArticle.NewsArticleResponse.class).data;
            NewsArticle article = articles.remove(0);
            cache.addAll(articles);
            return article;
        } catch (Exception e) {
            e.printStackTrace();
            return new NewsArticle();
        }
    }
}
