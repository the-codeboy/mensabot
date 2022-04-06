package ml.codeboy.thebot.quotes;

import com.google.gson.Gson;
import ml.codeboy.thebot.MensaBot;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class QuoteManager {
    private static QuoteManager instance = new QuoteManager();
    private HashMap<String, Person> persons = new HashMap<>();
    private ArrayList<Quote> quotes = new ArrayList<>();
    private Random random = new Random();
    private File saveFolder = new File("quotes");

    private QuoteManager() {
        loadPersons();
    }

    public static QuoteManager getInstance() {
        return instance;
    }

    private void loadPersons() {
        if (!saveFolder.exists())
            saveFolder.mkdirs();
        for (File file : saveFolder.listFiles()) {
            loadPerson(file);
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

    private void loadPerson(File file) {
        Person person = null;
        try {
            person = new Gson().fromJson(new FileReader(file), Person.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (person != null)
            registerPerson(person);
    }

    private void savePersons() {
        for (Person person : persons.values()) {
            save(person);
        }
    }

    public Collection<Person>getPersons(){
        return persons.values();
    }

    public void save(Person person) {
        try {
            saveFolder.mkdirs();
            FileWriter writer = new FileWriter(saveFolder + File.separator + person.getName() + ".json");
            new Gson().toJson(person, writer);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Person getOrCreate(String name) {
        return persons.computeIfAbsent(name, n -> {
            Person p = new Person();
            p.setName(n);
            return p;
        });
    }

    public Quote getRandomQuote() {
        return getRandomQuote(quotes);
    }

    public Quote getRandomQuote(String person) {
        Person p = getOrCreate(person);
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
}
