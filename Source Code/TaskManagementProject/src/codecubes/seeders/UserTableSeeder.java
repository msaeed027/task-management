package codecubes.seeders;

import codecubes.core.ModelManager;
import codecubes.models.User;

import java.util.HashMap;
import java.util.List;

/**
 * Created by msaeed on 1/27/2017.
 */
public class UserTableSeeder implements SeederContract {

    public void run() {
        HashMap parameters = new HashMap();
        parameters.put("email", "admin@gmail.com");
        List<User> users = ModelManager.getInstance().where(User.class, "where email = (:email)", parameters);
        if (users.size() == 0) {
            User user = new User();
            user.setEmail("admin@gmail.com");
            user.setPassword("123");
            user.setManager(true);
            ModelManager.getInstance().save(user);
        }
    }

}