package ml.codeboy.thebot.commands;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import ml.codeboy.thebot.MensaBot;
import ml.codeboy.thebot.apis.mongoDB.DatabaseManager;
import ml.codeboy.thebot.events.CommandEvent;
import org.bson.Document;

import javax.print.Doc;
import java.util.Arrays;
import java.util.Random;

public class JermaCommand extends Command {

    private int p = 0;

    private MongoCollection collection;

    private Document[] docs;

    private final Random rand;

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
        rand = new Random();
    }

    @Override
    public void run(CommandEvent event) {
        if(databaseSize!=collection.countDocuments())
        {
            databaseSize = collection.countDocuments();
            int i = 0;
            for(Object d : collection.find())
                docs[i++] = (Document) d;
            MensaBot.logger.info("Reloaded Jerma urls");
        }
        if(p==databaseSize)
        {
            shuffle(docs);
            p=0;
        }
        event.reply(docs[p++].getString("url"));
    }
    private void shuffle(Document[] a)
    {
        Document tmpDoc;
        int tmpInt = 0;
        for(int i = 0; i < a.length; i++)
        {
            tmpDoc = a[i];
            tmpInt = rand.nextInt(a.length);
            a[i] = a[tmpInt];
            a[tmpInt] = tmpDoc;
        }
    }
}
