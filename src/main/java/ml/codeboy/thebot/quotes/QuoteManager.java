package ml.codeboy.thebot.quotes;

import com.google.gson.Gson;
import ml.codeboy.thebot.MensaBot;
import ml.codeboy.thebot.apis.quoteAPI;
import org.bson.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class QuoteManager {
    private static QuoteManager instance = new QuoteManager();
    private HashMap<String, Person> persons = new HashMap<>();
    private ArrayList<Quote> quotes = new ArrayList<>();
    private Random random = new Random();

    private QuoteManager() {
        loadPersons();
    }

    public static QuoteManager getInstance() {
        return instance;
    }

    private void loadPersons() {
        for (String p : quoteAPI.getPersons()) {
            loadPerson(p);
        }
        MensaBot.logger.info("loaded " + persons.size() + " persons with a total of " + quotes.size() + " quotes");
    }

    public void registerPerson(Person person) {
        persons.put(person.getName(), person);
        for (Quote quote : person.getQuotes()) {
            quote.setPerson(person.getName());
        }
        quotes.addAll(person.getQuotes());
        MensaBot.logger.info("registered " + person.getName() + " with " + person.getQuotes().size() + " quotes");
    }

    private void loadPerson(String p) {
        Person person = new Person(p);
        Quote q = null;
        ArrayList<Quote> list = new ArrayList<>();
        for(Document d : quoteAPI.getQuotes(p)) {
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
        quotes.add(quote);
    }

    public ArrayList<Quote>getQuotes(String person)
    {
        return persons.get(person).getQuotes();
    }
}
