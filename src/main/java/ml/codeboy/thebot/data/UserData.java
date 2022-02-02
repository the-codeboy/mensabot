package ml.codeboy.thebot.data;

public class UserData {
    private String userId;
    private int bedTime=-1;

    public UserData(String userId) {
        this.userId = userId;
    }

    public int getBedTime() {
        return bedTime;
    }

    public void setBedTime(int bedTime) {
        this.bedTime = bedTime;
    }

    public String getId() {
        return userId;
    }
}
