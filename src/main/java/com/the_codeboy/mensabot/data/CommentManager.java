package com.the_codeboy.mensabot.data;

import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentManager {
    private static final CommentManager instance = new CommentManager();

    private final HashMap<String, ArrayList<String>> comments = new HashMap<>();

    public CommentManager() {
        for (UserData data : UserDataManager.getInstance().getAllUserData()) {
            if (data != null && data.getComments() != null)
                for (Comment c : data.getComments()) {
                    getComments(c.getMeal()).add(c.getContent());
                }
        }
    }

    public static CommentManager getInstance() {
        return instance;
    }

    public ArrayList<String> getComments(String meal) {
        return comments.computeIfAbsent(meal, e -> new ArrayList<>());
    }

    public void addComment(String meal, String comment, User user) {
        getComments(meal).add(comment);
        UserData data = UserDataManager.getInstance().getData(user);
        if (data != null) {
            data.getComments().add(new Comment(comment, meal));
            UserDataManager.getInstance().save(data);
        }
    }
}
