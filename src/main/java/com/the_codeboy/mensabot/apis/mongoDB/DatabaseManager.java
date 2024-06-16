package com.the_codeboy.mensabot.apis.mongoDB;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.the_codeboy.mensabot.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
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
