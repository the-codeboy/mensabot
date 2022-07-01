package ml.codeboy.thebot.apis.mongoDB;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import ml.codeboy.thebot.Config;
import ml.codeboy.thebot.MensaBot;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

public class databaseClass {

    private static databaseClass database_instance;

    static {
        database_instance = new databaseClass();
    }
    private MongoDatabase database;
    private MongoClient mongoClient;

    public databaseClass()
    {
        ConnectionString connectionString = new ConnectionString("mongodb+srv://"+Config.getInstance().mongoDB_username+":"+ Config.getInstance().mongoDB_passw +"@mensabot.wv6sk.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("quotes");
        try {
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = database.runCommand(command);
            MensaBot.logger.info("Connected successfully to server.");
        } catch (MongoException me) {
            MensaBot.logger.error("An error occurred while attempting to run a command: " + me);
        }
    }

    public static databaseClass getInstance(){return database_instance;}

    public MongoDatabase getDatabase(){return database;}
}
