package CheersMate.cheersmate.users.service;

import CheersMate.cheersmate.exception.CustomValidationException;
import CheersMate.cheersmate.users.entity.Users;
import CheersMate.cheersmate.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void saveUser(Users user) {
        try {
            validateDuplicateUser(user);
            userRepository.save(user);
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("This username is already taken.")) {
                throw new CustomValidationException("This username is already taken.", 400);
            } else if (e.getMessage().contains("This nickname is already taken.")) {
                throw new CustomValidationException("This nickname is already taken.", 400);
            } else {
                throw e;
            }
        }
    }

    private void validateDuplicateUser(Users user) {
        Users existingUserByEmail = userRepository.findByEmail(user.getEmail());
        if (existingUserByEmail != null) {
            throw new IllegalStateException("This username is already taken.");
        }

        Users existingUserByUserID = userRepository.findByNickname(user.getNickname());
        if (existingUserByUserID != null) {
            throw new IllegalStateException("This nickname is already taken.");
        }
    }

    public List<Users> findAllUsers() {
        return userRepository.findAll();
    }

    public Users findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Users login(String email, String password) {
        Users user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public Users findEmail(String tell, String nickname) {
        return userRepository.findByTellAndNickname(tell, nickname);
    }

    public Users findPass(String email, String tell) {
        return userRepository.findByEmailAndTell(email, tell);
    }

    @Transactional
    public void update(String email, String tell, String nickname) {
        Users user = userRepository.findByEmail(email);
        user.setTell(tell);
        user.setNickname(nickname);
    }

    @Transactional
    public void deleteUser(Users user) {
        userRepository.delete(user);
    }

    @Transactional
    public void changePassword(String email, String newPassword) {
        Users user = userRepository.findByEmail(email);
        user.setPassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public String generateTempPassword() {
        int length = 8;  // 임시 비밀번호 길이
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}