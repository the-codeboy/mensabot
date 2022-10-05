package ml.codeboy.thebot.commands;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import ml.codeboy.thebot.apis.mongoDB.DatabaseManager;
import ml.codeboy.thebot.events.CommandEvent;
import org.bson.Document;

import static ml.codeboy.thebot.util.Util.shuffle;

public class JermaCommand extends Command {

    private int p = 0;

    private Document[] docs;

    private long databaseSize = -1;

    private MongoClient client;

    public JermaCommand() {
        super("jerma", "Sends a Jerma");
        updateDocs();
        setGuildOnlyCommand(false);
    }

    private void dbConnect()
    {
        client = MongoClients.create(DatabaseManager.getInstance().getSettings());
    }

    private void dbClose()
    {
        client.close();
    }

    private void updateDocs()
    {
        dbConnect();
        MongoCollection collection = client.getDatabase("text").getCollection("jerma");
        databaseSize = collection.countDocuments();
        docs = new Document[(int)databaseSize];
        {
            int i = 0;
            for(Object d : collection.find())
                docs[i++] = (Document) d;
        }
        shuffle(docs);
        dbClose();
    }

    private long getCollectionSize()
    {
        dbConnect();
        long ret = client.getDatabase("text").getCollection("jerma").countDocuments();
        dbClose();
        return ret;
    }

    @Override
    public void run(CommandEvent event) {
        dbConnect();
        System.out.println("HALLO?");
        MongoCollection collection = client.getDatabase("text").getCollection("jerma");
        long collSize = collection.countDocuments();
        if(databaseSize!=collSize)
        {
            databaseSize = collSize;
            int i = 0;
            docs = new Document[(int)databaseSize];
            for(Object d : collection.find())
                docs[i++] = (Document) d;
            shuffle(docs);
            p = 0;
            getLogger().info("Reloaded Jerma urls");
        }
        dbClose();
        if(p==databaseSize)
        {
            shuffle(docs);
            p=0;
        }
        event.reply(docs[p++].getString("url"));
    }
}
