package ml.codeboy.thebot.apis.mongoDB;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import ml.codeboy.thebot.Config;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DatabaseManager {

    private static final DatabaseManager database_instance;

    static {
        DatabaseManager manager = null;
        try {
            manager = new DatabaseManager();
        } catch (Exception e) {
            LoggerFactory.getLogger(DatabaseManager.class).error("failed to create DatabaseManager", e);
        }
        database_instance = manager;
    }

    private final Logger logger
            = LoggerFactory.getLogger(getClass());
    private final ConnectionString connectionString;

    private final MongoClientSettings settings;

    public DatabaseManager() {
        //Create the connection string and settings
        connectionString = new ConnectionString(Config.getInstance().mongoDB_URL);
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
    }

    public static DatabaseManager getInstance() {
        return database_instance;
    }

    public MongoClientSettings getSettings() {
        return settings;
    }
}
