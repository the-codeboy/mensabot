package ml.codeboy.thebot.data;

import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.util.*;

public class UserDataManager {
    private static final String userDataFolder = "users";

    private static final UserDataManager instance = new UserDataManager();
    private final HashMap<String, UserData> userData = new HashMap<>();

    private final long karmaTopUpdate = 600000;
    private long lastUpdatedKarmaTop = 0;

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

    private List<UserData> karmaSorted;

    private UserDataManager() {
        new Thread(this::loadUserData).start();
    }

    private void loadUserData() {
        File folder = new File(userDataFolder);
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                try {
                    userData.put(file.getName(), loadData(file.getName()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        updateKarmaTop();
    }


    public List<UserData> getKarmaSorted() {
        if (System.currentTimeMillis() - karmaTopUpdate > lastUpdatedKarmaTop) {
            updateKarmaTop();
        }
        karmaSorted.sort(Comparator.comparingInt(UserData::getKarma).reversed());
        return karmaSorted;
    }

    private void updateKarmaTop() {
            karmaSorted = new ArrayList<>(getAllUserData());
            karmaSorted.removeIf(d->d.getKarma()==0);
            karmaSorted.sort(Comparator.comparingInt(UserData::getKarma).reversed());
//            karmaSorted = karmaSorted.subList(0, 20);
            lastUpdatedKarmaTop = System.currentTimeMillis();
    }
}
