package ml.codeboy.thebot.apis.mongoDB;

import com.mongodb.client.*;
import ml.codeboy.thebot.data.Comment;
import ml.codeboy.thebot.data.UserData;

import java.util.ArrayList;

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
        MongoCollection<UserData> collectionUserData = db.getCollection(userID,UserData.class);
        UserData d = collectionUserData.find(exists("susCount")).first();
        MongoCollection<Comment> collectionComments = db.getCollection(userID,Comment.class);
        FindIterable<Comment> comments = collectionComments.find(exists("susCount",false));
        ArrayList<Comment> cmts = new ArrayList<>();
        for(Comment c : comments)
        {
            cmts.add(c);
        }
        d.setComments(cmts);
        client.close();
        return d;
    }

    public static ArrayList<String> getUserIds()
    {
        MongoClient client = MongoClients.create(DatabaseManager.getInstance().getSettings());
        MongoDatabase db = client.getDatabase("users");
        ArrayList<String> ret = new ArrayList<>();
        db.listCollectionNames().iterator().forEachRemaining(ret::add);
        return ret;
    }
}
