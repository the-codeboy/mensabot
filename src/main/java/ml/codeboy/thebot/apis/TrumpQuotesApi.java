package ml.codeboy.thebot.apis;

public class TrumpQuotesApi extends CachedAPI<TrumpQuote> {
    private static final TrumpQuotesApi api = new TrumpQuotesApi();

    public TrumpQuotesApi() {
        super(10);
    }

    public static TrumpQuotesApi getApi() {
        return api;
    }

    @Override
    protected TrumpQuote requestObject() {
        try {
            String json = readUrl("https://api.tronalddump.io/random/quote");
            TrumpQuote quote = gson.fromJson(json, TrumpQuote.class);
            return quote;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new TrumpQuote();
    }
}
