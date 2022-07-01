package ml.codeboy.thebot.apis.mongoDB;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import ml.codeboy.thebot.Config;

public class databaseClass {

    private static databaseClass database_instance;

    static {
        database_instance = new databaseClass();
    }
    private MongoDatabase database;
    private MongoClient mongoClient;

    public databaseClass()
    {
        ConnectionString connectionString = new ConnectionString("mongodb://mensa-bot-test-nils:"+ Config.getInstance().mongoDB_passw +"@quotes.wv6sk.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        mongoClient = MongoClients.create(settings);
        System.out.println("Connected to client: "+mongoClient);
        database = mongoClient.getDatabase("Quotes");
        System.out.println("Connected to database: "+database);
    }

    public static databaseClass getInstance(){return database_instance;}

    public MongoDatabase getDatabase(){return database;}
}
