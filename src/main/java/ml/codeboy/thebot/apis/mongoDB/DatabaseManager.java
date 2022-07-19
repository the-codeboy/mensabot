package ml.codeboy.thebot.apis.mongoDB;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import ml.codeboy.thebot.Config;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {

    private static final DatabaseManager database_instance;

    static {
        database_instance = new DatabaseManager();
    }

    private final MongoDatabase quotesDatabase;
    private final MongoDatabase textDatabase;
    private final MongoCollection karma;
    private final MongoClient mongoClient;
    private final Logger logger
            = LoggerFactory.getLogger(getClass());

    public DatabaseManager() {
        ConnectionString connectionString = new ConnectionString(Config.getInstance().mongoDB_URL);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        mongoClient = MongoClients.create(settings);
        quotesDatabase = mongoClient.getDatabase("quotes");
        textDatabase = mongoClient.getDatabase("text");
        karma = mongoClient.getDatabase("karma").getCollection("karma");
        try {
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = quotesDatabase.runCommand(command);
            logger.info("Connected successfully to server.");
        } catch (MongoException me) {
            logger.error("An error occurred while attempting to run a command: " + me);
        }
    }

    public static DatabaseManager getInstance(){return database_instance;}

    public MongoDatabase getQuotesDatabase(){return quotesDatabase;}
    public MongoCollection getKarma(){return karma;}
    public MongoDatabase getTextDatabase(){return textDatabase;}
}
