package service;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yunheekim on 2017. 5. 25..
 */
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public void create(User user) {
        DataBase.addUser(user);
    }

    public boolean login(User loginUser) {
        User user = DataBase.findUserById(loginUser.getUserId());

        if(user == null) {
            return false;
        }

        return loginUser.getPassword().equals(user.getPassword());
    }

    public String list() {

        StringBuilder sb = new StringBuilder();

        AtomicInteger atomicInteger = new AtomicInteger(1);

        DataBase.findAll().stream().forEach(u -> {
            sb.append("<tr>");
            sb.append("<th scope=\"row\">");
            sb.append(atomicInteger.getAndIncrement());
            sb.append("</th>");
            sb.append("<td>");
            sb.append(u.getUserId());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(u.getName());
            sb.append("</td>");
            sb.append("<td>");
            sb.append(u.getEmail());
            sb.append("</td>");
            sb.append("<td><a href=\"#\" class=\"btn btn-success\" role=\"button\">수정</a></td>");
            sb.append("</tr>");
        });

        return sb.toString();
    }
}
