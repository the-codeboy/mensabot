package ml.codeboy.thebot.apis;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.net.URLEncoder;
import java.util.ArrayList;

public class RhymeApi extends API {
    private static final RhymeApi instance = new RhymeApi();

    public static RhymeApi getInstance() {
        return instance;
    }

    public ArrayList<String> getRhymingWords(String word) {
        try {
            String json = readUrl("https://api.datamuse.com/words?rel_rhy=" + URLEncoder.encode(word));
            JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
            ArrayList<String> words = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++)
                words.add(jsonArray.get(i).getAsJsonObject().get("word").getAsString());
            return words;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
