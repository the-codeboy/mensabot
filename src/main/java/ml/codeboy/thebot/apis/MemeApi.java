package ml.codeboy.thebot.apis;

public class MemeApi extends CachedAPI<Meme> {
    private static final MemeApi instance = new MemeApi();

    public static MemeApi getInstance() {
        return instance;
    }

    @Override
    protected Meme requestObject() {
        try {
            String json = readUrl("https://meme-api.herokuapp.com/gimme");
            Meme meme = gson.fromJson(json, Meme.class);
            return meme;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Meme();
    }
}
