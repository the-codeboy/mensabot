package ml.codeboy.thebot.data;

import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;

public class UserDataManager {
    private static final String userDataFolder = "users";

    private static final UserDataManager instance = new UserDataManager();
    private final HashMap<String, UserData> userData = new HashMap<>();

    private UserDataManager() {
        File folder = new File(userDataFolder);
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                try {
                    loadData(file.getName());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static UserDataManager getInstance() {
        return instance;
    }

    public UserData getData(User user) {
        return getData(user.getId());
    }

    public UserData getData(String userId) {
        UserData data = userData.get(userId);
        if (data != null) {
            return data;
        }
        try {
            return loadData(userId);
        } catch (FileNotFoundException ignored) {
        }
        data = new UserData(userId);
        userData.put(userId, data);
        return data;
    }


    public UserData loadData(User user) throws FileNotFoundException {
        return loadData(user.getId());
    }


    private UserData loadData(String id) throws FileNotFoundException {
        UserData data = new Gson().fromJson(new FileReader(userDataFolder + File.separator + id), UserData.class);
        return data;
    }

    public Collection<UserData> getAllUserData() {
        return userData.values();
    }

    public void save(UserData data) {
        try {
            new File(userDataFolder).mkdirs();
            FileWriter writer = new FileWriter(userDataFolder + File.separator + data.getId());
            new Gson().toJson(data, writer);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
