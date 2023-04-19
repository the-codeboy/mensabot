package ml.codeboy.thebot.apis.mongoDB;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import ml.codeboy.thebot.data.Comment;
import ml.codeboy.thebot.data.UserData;

import static com.mongodb.client.model.Filters.exists;

public class DatabaseUserAPI {
    public static void saveUser(UserData user)
    {
        String userID = user.getUserId();
        MongoClient client = MongoClients.create(DatabaseManager.getInstance().getSettings());
        MongoDatabase db = client.getDatabase("users");
        db.getCollection(userID).drop();

        db.getCollection(userID,UserData.class).insertOne(user);
        db.getCollection(userID,Comment.class).insertMany(user.getComments());
        client.close();
    }

    public static UserData getUser(String userID)
    {
        MongoClient client = MongoClients.create(DatabaseManager.getInstance().getSettings());
        MongoDatabase db = client.getDatabase("users");
        MongoCollection<UserData> collection = db.getCollection(userID,UserData.class);
        UserData d = collection.find(exists("index")).first();
        client.close();
        return null;
    }
}
