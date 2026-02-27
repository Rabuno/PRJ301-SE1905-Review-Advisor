package application.services;

import application.ports.IUserRepository;
import domain.entities.User;

public class AuthService {
    private final IUserRepository userRepository;

    public AuthService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String username, String rawPassword) throws Exception {
        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            throw new Exception("Tài khoản không tồn tại.");
        }
        
        // Trong môi trường production, rawPassword cần được băm và so sánh với mã băm trong DB.
        // Hiện tại dùng string equals cho mức độ nguyên mẫu (prototype).
        if (!user.getPassword().equals(rawPassword)) {
            throw new Exception("Sai mật khẩu.");
        }
        
        return user;
    }
}