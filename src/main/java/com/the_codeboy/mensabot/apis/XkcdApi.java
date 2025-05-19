package com.the_codeboy.mensabot.apis;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class XkcdApi extends API {
    private static final XkcdApi instance = new XkcdApi();

    public static XkcdApi getInstance() {
        return instance;
    }

    private static final String BASE_URL = "https://xkcd.com";
    private static Xkcd newest;

    public XkcdApi() {
        newest = getNewest(false);
    }

    public Xkcd getNewest(boolean cached) {
        if (cached && newest != null) {
            ZonedDateTime date = java.time.ZonedDateTime.now(ZoneId.of("US/Eastern"));
            if (Integer.parseInt(newest.getDay()) == date.getDayOfMonth() && Integer.parseInt(newest.getMonth()) == date.getMonth().getValue() && Integer.parseInt(newest.getYear()) == date.getYear()) {
                return newest;
            }
        }
        Xkcd loaded = load("");
        if (loaded != null) {
            newest = loaded;
        }
        return newest;
    }

    private Xkcd load(String num) {
        try {
            String json = readUrl(BASE_URL + num + "/info.0.json");
            return gson.fromJson(json, Xkcd.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getMax() {
        return getNewest(true).getNum();
    }

    public Xkcd get(int number) {
        return load("/" + number);
    }

    public Xkcd get(String num) {
        return load("/" + num);
    }

    public Xkcd get() {
        return get((int) Math.floor(Math.random() * getMax()));
    }
}
