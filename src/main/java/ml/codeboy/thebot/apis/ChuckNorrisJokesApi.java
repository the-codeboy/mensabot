package ml.codeboy.thebot.apis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ChuckNorrisJokesApi extends CachedAPI<String> {
    private static final ChuckNorrisJokesApi instance = new ChuckNorrisJokesApi();

    public ChuckNorrisJokesApi() {
        super(10);
    }

    public static ChuckNorrisJokesApi getInstance() {
        return instance;
    }

    @Override
    protected String requestObject() {
        try {
            String json = readUrl("https://api.chucknorris.io/jokes/random");
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            return obj.get("value").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Can´t find joke";
    }
}
