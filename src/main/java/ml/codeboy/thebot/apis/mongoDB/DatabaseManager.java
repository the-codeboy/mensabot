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
    private final Logger logger
            = LoggerFactory.getLogger(getClass());
    private final ConnectionString connectionString;

    private final MongoClientSettings settings;

    public DatabaseManager() {
        connectionString = new ConnectionString(Config.getInstance().mongoDB_URL);
        settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        logger.debug("Database_instance build");
    }

    public static DatabaseManager getInstance() {
        return database_instance;
    }

    public MongoClientSettings getSettings() {
        return settings;
    }
}
