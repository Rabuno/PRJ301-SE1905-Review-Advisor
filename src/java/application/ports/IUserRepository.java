package application.ports;

import domain.entities.User;
import java.util.List;

public interface IUserRepository {
    User findByUsername(String username);

    boolean registerUser(User user, String roleName);

    List<User> getAllUsers();

    boolean updateUserRole(String userId, String roleId);
}
