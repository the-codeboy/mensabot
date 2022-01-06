package ml.codeboy.thebot.apis;

import com.google.gson.JsonParser;

public class AdviceApi extends CachedAPI<String> {
    private static final AdviceApi instance = new AdviceApi();

    public static AdviceApi getInstance() {
        return instance;
    }

    @Override
    protected String requestObject() {
        try {
            return JsonParser.parseString(readUrl("https://api.adviceslip.com/advice")).getAsJsonObject()
                    .get("slip").getAsJsonObject().get("advice").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
