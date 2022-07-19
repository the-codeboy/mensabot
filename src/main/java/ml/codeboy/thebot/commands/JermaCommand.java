package ml.codeboy.thebot.commands;

import com.mongodb.client.MongoCollection;
import ml.codeboy.thebot.apis.mongoDB.DatabaseManager;
import ml.codeboy.thebot.events.CommandEvent;
import org.bson.Document;

import static ml.codeboy.thebot.util.Util.shuffle;

public class JermaCommand extends Command {

    private int p = 0;

    private final MongoCollection collection;

    private Document[] docs;

    private long databaseSize = -1;

    public JermaCommand() {
        super("jerma", "Sends a Jerma");
        collection = DatabaseManager.getInstance().getTextDatabase().getCollection("jerma");
        databaseSize = collection.countDocuments();
        docs = new Document[(int)databaseSize];
        {
            int i = 0;
            for(Object d : collection.find())
                docs[i++] = (Document) d;
        }
        shuffle(docs);
    }

    @Override
    public void run(CommandEvent event) {
        if(databaseSize!=collection.countDocuments())
        {
            databaseSize = collection.countDocuments();
            int i = 0;
            docs = new Document[(int)databaseSize];
            for(Object d : collection.find())
                docs[i++] = (Document) d;
            shuffle(docs);
            p = 0;
            getLogger().info("Reloaded Jerma urls");
        }
        if(p==databaseSize)
        {
            shuffle(docs);
            p=0;
        }
        event.reply(docs[p++].getString("url"));
    }
}
