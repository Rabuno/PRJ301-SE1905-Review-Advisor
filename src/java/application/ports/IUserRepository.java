package application.ports;

import domain.entities.User;
import java.util.List;

public interface IUserRepository {
    User findByUsername(String username);

    // Lightweight lookup (used by services that need account age / metadata without loading permissions).
    User findById(String userId);

    boolean registerUser(User user, String roleName);

    List<User> getAllUsers();

    boolean updateUserRole(String userId, String roleId);
}
