package application.services;

import application.ports.IUserRepository;
import application.util.HashUtil;
import domain.entities.User;

public class AuthService {
    private final IUserRepository userRepository;

    public AuthService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String username, String rawPassword) throws Exception {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new Exception("Account does not exist.");
        }

        String hashedPassword = HashUtil.generateSHA256(rawPassword);

        if (!user.getPassword().equals(hashedPassword)) {
            throw new Exception("Incorrect password.");
        }

        return user;
    }

    public boolean processRegistration(String username, String password, boolean isMerchant) throws Exception {
        if (userRepository.findByUsername(username) != null) {
            throw new Exception("Username already exists.");
        }

        String userId = java.util.UUID.randomUUID().toString();

        String hashedPassword = HashUtil.generateSHA256(password);

        User newUser = new User(userId, username, hashedPassword);

        String roleName = isMerchant ? "MERCHANT" : "CUSTOMER";

        return userRepository.registerUser(newUser, roleName);
    }
}
