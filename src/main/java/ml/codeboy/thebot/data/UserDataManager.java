package ml.codeboy.thebot.data;

import com.google.gson.Gson;
import ml.codeboy.thebot.apis.mongoDB.DatabaseUserAPI;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.io.*;
import java.util.*;

public class UserDataManager {
    private static final String userDataFolder = "users";
    private static final UserDataManager instance = new UserDataManager();
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final HashMap<String, UserData> userData = new HashMap<>();

    private final long karmaTopUpdate = 600000;
    private final Thread initThread;
    private long lastUpdatedKarmaTop = 0;
    private List<UserData> karmaSorted;

    private UserDataManager() {
        initThread = new Thread(this::loadUserData);
        initThread.start();
    }

    public static UserDataManager getInstance() {
        return instance;
    }

    public String moveDataToCloud()
    {
        String ret = "";
        File folder = new File(userDataFolder);
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                try {
                    DatabaseUserAPI.saveUser(new Gson().fromJson(new FileReader(userDataFolder + File.separator + file.getName()), UserData.class));
                    ret += "Moved "+file.getName()+" to cloud\n";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    public UserData getData(User user) {
        return getData(user.getId());
    }

    public UserData loadData(User user) {
        return loadData(user.getId());
    }

    private UserData loadData(String id) {
        return DatabaseUserAPI.getUser(id);
    }

    public void save(UserData data) {
        DatabaseUserAPI.saveUser(data);
    }

    public UserData getData(String userId) {
        waitTilInit();
        UserData data = userData.get(userId);
        if (data != null) {
            return data;
        }
        return loadData(userId);
    }

    public Collection<UserData> getAllUserData() {
        waitTilInit();
        return userData.values();
    }

    private void waitTilInit() {
        if (initThread != null) {
            try {
                initThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadUserData() {
        for(String s: DatabaseUserAPI.getUserIds())
        {
            userData.put(s, loadData(s));
        }
        logger.info("finished loading user data for users");
    }


    public List<UserData> getKarmaSorted() {
        if (System.currentTimeMillis() - karmaTopUpdate > lastUpdatedKarmaTop) {
            updateKarmaTop();
        }
        return karmaSorted;
    }

    public List<UserData> getKarmaTop()
    {
        return DatabaseUserAPI.getTopN("karma",10);
    }
    public List<UserData> getKarmaBottom()
    {
        return DatabaseUserAPI.getBottomN("karma",10);
    }

    private void updateKarmaTop() {
        karmaSorted = new ArrayList<>(getAllUserData());
        karmaSorted.removeIf(d -> d.getKarma() == 0);
        karmaSorted.sort(Comparator.comparingInt(UserData::getKarma).reversed());
        //karmaSorted = DatabaseUserAPI.getTopN("karma",10);
//            karmaSorted = karmaSorted.subList(0, 20);
        lastUpdatedKarmaTop = System.currentTimeMillis();
    }
}
