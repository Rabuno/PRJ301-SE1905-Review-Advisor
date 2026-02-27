package application.ports;

import domain.entities.User;

public interface IUserRepository {
    User findByUsername(String username);
}
