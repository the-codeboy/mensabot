package com.the_codeboy.mensabot.commands;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.the_codeboy.mensabot.apis.mongoDB.DatabaseManager;
import com.the_codeboy.mensabot.events.CommandEvent;
import org.bson.Document;

import static com.the_codeboy.mensabot.util.Util.shuffle;

public class JermaCommand extends Command {

    private int p = 0;

    private Document[] docs;

    private long databaseSize = -1;

    private MongoClient client;

    private boolean dbConnected = false;

    public JermaCommand() {
        super("jerma", "Sends a Jerma");
        updateDocs();
        setGuildOnlyCommand(false);
    }

    /**
     * Open the connection to the Database and set the appropriate flag
     */
    private void dbConnect() {
        if (!dbConnected) {
            try {
                client = MongoClients.create(DatabaseManager.getInstance().getSettings());
                dbConnected = true;
            } catch (Exception e) {
                getLogger().error("failed to connect to DB", e);
            }
        } else {
            getLogger().error("Not able to open the connection because it is already open");
        }
    }

    /**
     * Close the connection and set the flag
     */
    private void dbClose() {
        if (dbConnected) {
            client.close();
            dbConnected = false;
        } else {
            getLogger().error("Not able to close the client because it has already been closed");
        }
    }

    /**
     * Load the 'jermas' and shuffle them
     */
    private void updateDocs() {
        dbConnect();
        if (!dbConnected)
            return;
        MongoCollection collection = client.getDatabase("text").getCollection("jerma");
        databaseSize = collection.countDocuments();
        docs = new Document[(int) databaseSize];
        {
            int i = 0;
            for (Object d : collection.find())
                docs[i++] = (Document) d;
        }
        shuffle(docs);
        dbClose();
    }

    /**
     * Get the amount of 'jermas' in the db
     *
     * @return the amount of 'jermas'
     */
    private long getCollectionSize() {
        dbConnect();
        long ret = client.getDatabase("text").getCollection("jerma").countDocuments();
        dbClose();
        return ret;
    }

    @Override
    public void run(CommandEvent event) {
        dbConnect();
        MongoCollection collection = client.getDatabase("text").getCollection("jerma");
        long collSize = collection.countDocuments();
        if (databaseSize != collSize) {
            databaseSize = collSize;
            int i = 0;
            docs = new Document[(int) databaseSize];
            for (Object d : collection.find())
                docs[i++] = (Document) d;
            shuffle(docs);
            p = 0;
            getLogger().info("Reloaded Jerma urls");
        }
        dbClose();
        if (p == databaseSize) {
            shuffle(docs);
            p = 0;
        }
        event.reply(docs[p++].getString("url"));
    }
}
