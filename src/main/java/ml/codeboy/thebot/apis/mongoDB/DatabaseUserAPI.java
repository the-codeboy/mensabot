package ml.codeboy.thebot.apis.mongoDB;

import com.mongodb.client.*;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import ml.codeboy.thebot.data.Comment;
import ml.codeboy.thebot.data.UserData;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;

public class DatabaseUserAPI {
    public static void saveUser(UserData user) {
        String userID = user.getUserId();
        MongoClient client = MongoClients.create(DatabaseManager.getInstance().getSettings());
        client.getDatabase("users")
                .getCollection("users", UserData.class)
                .replaceOne(eq("_id", userID), user,new ReplaceOptions().upsert(true));
        client.close();
    }

    public static UserData getUser(String userID) {
        MongoClient client = MongoClients.create(DatabaseManager.getInstance().getSettings());
        MongoDatabase db = client.getDatabase("users");
        MongoCollection<UserData> collectionUserData = db.getCollection("users", UserData.class);
        UserData d = collectionUserData.find(eq("_id", userID)).first();
        client.close();
        return d;
    }

    public static ArrayList<String> getUserIds() {
        MongoClient client = MongoClients.create(DatabaseManager.getInstance().getSettings());
        FindIterable<UserData> docs = client.getDatabase("users").getCollection("users",UserData.class).find();
        ArrayList<String> ret = new ArrayList<>();
        docs.iterator().forEachRemaining(x->ret.add(x.getUserId()));
        return ret;
    }

    public static List<UserData> findUsers(List<? extends Bson> filter)
    {
        MongoCollection<UserData> collection = MongoClients.create(DatabaseManager.getInstance().getSettings())
                .getDatabase("users")
                .getCollection("users",UserData.class);
        AggregateIterable<UserData> result = collection.aggregate(filter);
        List<UserData> ret = new ArrayList<>();
        result.iterator().forEachRemaining(ret::add);
        return ret;
    }

    public static List<UserData> getTopN(String karma, int i) {
        return findUsers(Arrays.asList(
                new Document("$match", new Document("karma", new Document("$ne", 0))),
                new Document("$sort", new Document("karma", -1)),
                new Document("$limit", i)
        ));
    }
    public static List<UserData> getBottomN(String karma, int i) {
        return findUsers(Arrays.asList(
                new Document("$match", new Document("karma", new Document("$ne", 0))),
                new Document("$sort", new Document("karma", 1)),
                new Document("$limit", i)
        ));
    }
}
