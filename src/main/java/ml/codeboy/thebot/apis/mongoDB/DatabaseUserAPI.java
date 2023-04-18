package ml.codeboy.thebot.apis.mongoDB;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import ml.codeboy.thebot.data.UserData;
import org.bson.BsonDocument;
import org.bson.Document;

public class DatabaseUserAPI {
    public static void saveUser(UserData user)
    {
        String userID = user.getId();
        MongoClient client = MongoClients.create(DatabaseManager.getInstance().getSettings());
        MongoDatabase db = client.getDatabase("users");
        Document userDoc = new Document();
        userDoc.append("bedTime",user.getBedTime());
        userDoc.append("karma",user.getKarma());
        userDoc.append("susCount",user.getSusCount());
        userDoc.append("name",user.getId());
        userDoc.append("ratings",user.getRatings());
        userDoc.append("restaurantRatings",user.getRestaurantRatings());
        userDoc.append("comments",user.getComments());
        userDoc.append(userID,userID);
        db.getCollection(userID).updateMany(new Document().append(userID,userID),userDoc);
        client.close();
    }
}
