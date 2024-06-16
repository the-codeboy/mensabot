package com.the_codeboy.mensabot.apis;

public class InsultApi extends CachedAPI<String> {
    private static final InsultApi instance = new InsultApi();

    public InsultApi() {
        super(1);
    }

    public static InsultApi getInstance() {
        return instance;
    }

    @Override
    protected String requestObject() {
        try {
            return readUrl("https://evilinsult.com/generate_insult.php?lang=en");
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
