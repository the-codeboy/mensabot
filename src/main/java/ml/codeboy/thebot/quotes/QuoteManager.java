package ml.codeboy.thebot.quotes;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoIterable;
import ml.codeboy.thebot.apis.mongoDB.DatabaseManager;
import ml.codeboy.thebot.apis.mongoDB.DatabaseQuoteAPI;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class QuoteManager {
    private static final QuoteManager instance = new QuoteManager();
    private final HashMap<String, Person> persons = new HashMap<>();
    private final ArrayList<Quote> quotes = new ArrayList<>();
    private final Random random = new Random();
    private final Logger logger
            = LoggerFactory.getLogger(getClass());

    private QuoteManager() {
        loadPersons();
    }

    public static QuoteManager getInstance() {
        return instance;
    }

    private void loadPersons() {
        MongoClient client= MongoClients.create(DatabaseManager.getInstance().getSettings());
        for (String p : DatabaseQuoteAPI.getPersons(client)) {
            loadPerson(p, client);
        }
        client.close();
        logger.info("loaded " + persons.size() + " persons with a total of " + quotes.size() + " quotes");
    }

    public void registerPerson(Person person) {
        persons.put(person.getName(), person);
        for (Quote quote : person.getQuotes()) {
            quote.setPerson(person.getName());
        }
        quotes.addAll(person.getQuotes());
        logger.info("registered " + person.getName() + " with " + person.getQuotes().size() + " quotes");
    }

    private void loadPerson(String p, MongoClient client) {
        Person person = new Person(p);
        Quote q = null;
        ArrayList<Quote> list = new ArrayList<>();
        for(Document d : DatabaseQuoteAPI.getQuotes(p, client)) {
            q = new Quote(
                    d.getString("content"),
                    d.getLong("time"),
                    d.getString("name"),
                    d.getString("authorId"));
            list.add(q);
        }
        person.setQuotes(list);
        if (person != null)
            registerPerson(person);
    }

    public Collection<Person>getPersons(){
        return persons.values();
    }

    public Quote getRandomQuote() {
        return getRandomQuote(quotes);
    }

    public Quote getRandomQuote(String person) {
        Person p = persons.get(person);
        if(p==null){
            p = new Person();
            p.setName(person);
        }
        if (p.getQuotes().isEmpty())
            return null;
        return getRandomQuote(p.getQuotes());
    }

    public Quote getRandomQuote(ArrayList<Quote> quotes) {
        return quotes.get(random.nextInt(quotes.size()));
    }

    public String getRandomQuoteContent() {
        return getRandomQuote().getContent();
    }

    public void addQuote(Quote quote) {
        try {
            persons.get(quote.getPerson()).getQuotes().add(quote);
        }catch (NullPointerException e)
        {
            persons.put(quote.getPerson(),new Person(quote.getPerson()));
            persons.get(quote.getPerson()).getQuotes().add(quote);
        }
        quotes.add(quote);
    }

    /**
     * This method returns all quotes from the given person
     * @param person
     * @return A list of all the quotes
     */
    public ArrayList<Quote>getQuotes(String person)
    {
        return persons.get(person).getQuotes();
    }
}
