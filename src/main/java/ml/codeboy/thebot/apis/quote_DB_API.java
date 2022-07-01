package ml.codeboy.thebot.apis;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import ml.codeboy.thebot.apis.mongoDB.databaseClass;
import ml.codeboy.thebot.events.CommandEvent;
import ml.codeboy.thebot.quotes.Quote;
import ml.codeboy.thebot.quotes.QuoteManager;
import org.bson.Document;

public class quote_DB_API {
    /**
     * Saves the given quote in the database and adds ist to the quote manager
     * @param event
     * @param name
     * @param content
     */
    public static void saveQuote(CommandEvent event, String name, String content) {
        QuoteManager.getInstance().addQuote(new Quote(content,System.currentTimeMillis(),name,event.getMember().getId()));
        Document quote = new Document("content", content);
        quote.append("time", System.currentTimeMillis());
        quote.append("authorId", event.getMember().getId());
        quote.append("name",name);
        databaseClass.getInstance().getDatabase().getCollection(name.toLowerCase()).insertOne(quote);
    }

    /**
     * Returns all saved persons
     * @return
     */
    public static MongoIterable<String> getPersons(){
         return databaseClass.getInstance().getDatabase().listCollectionNames();
    }

    /**
     * Returns all the quotes of the given person
     * @param name
     * @return
     */
    public static FindIterable<Document> getQuotes(String name)
    {
        return databaseClass.getInstance().getDatabase().getCollection(name.toLowerCase()).find();
    }
}
