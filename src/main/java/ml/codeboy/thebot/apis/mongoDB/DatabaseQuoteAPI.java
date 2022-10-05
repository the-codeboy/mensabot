package ml.codeboy.thebot.apis.mongoDB;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoIterable;
import ml.codeboy.thebot.quotes.Quote;
import ml.codeboy.thebot.quotes.QuoteManager;
import org.bson.Document;

public class DatabaseQuoteAPI {
    /**
     * Saves the given quote in the database and adds it to the quote manager
     * @param q
     */
    public static void saveQuote(Quote q) {
        QuoteManager.getInstance().addQuote(q);
        Document quote = new Document("content", q.getContent());
        quote.append("time", q.getTime());
        quote.append("authorId", q.getAuthorId());
        quote.append("name",q.getPerson());
        MongoClient client = MongoClients.create(DatabaseManager.getInstance().getSettings());
        client.getDatabase("quotes").getCollection(q.getPerson().toLowerCase()).insertOne(quote);
        client.close();
    }

    /**
     * Returns all saved persons
     * @return
     */
    public static MongoIterable<String> getPersons(MongoClient client){
        MongoIterable<String> ret = client.getDatabase("quotes").listCollectionNames();
        return ret;
    }

    /**
     * Returns all the quotes of the given person
     * @param name
     * @return
     */
    public static FindIterable<Document> getQuotes(String name, MongoClient client)
    {
        FindIterable<Document> ret = client.getDatabase("quotes").getCollection(name.toLowerCase()).find();
        return ret;
    }
}
