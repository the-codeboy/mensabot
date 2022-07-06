package ml.codeboy.thebot.commands;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import ml.codeboy.thebot.MensaBot;
import ml.codeboy.thebot.apis.mongoDB.DatabaseManager;
import ml.codeboy.thebot.events.CommandEvent;
import org.bson.Document;

import java.util.Arrays;
import java.util.Random;

public class JermaCommand extends Command {

    private int p = 0;

    private MongoCollection collection;

    private FindIterable docs;

    private final Random rand;

    private long databaseSize = -1;

    public JermaCommand() {
        super("jerma", "Sends a Jerma");
        collection = DatabaseManager.getInstance().getTextDatabase().getCollection("jerma");
        databaseSize = collection.countDocuments();
        docs = collection.find();
        rand = new Random();
    }

    @Override
    public void run(CommandEvent event) {
        if(databaseSize!=collection.countDocuments())
        {
            databaseSize = collection.countDocuments();
            docs = collection.find();
            MensaBot.logger.info("Reloaded Jerma urls");
        }
        event.reply(((Document)docs.limit(1).skip(rand.nextInt((int)databaseSize)).first()).getString("url"));
    }
}
