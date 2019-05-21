package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Primary
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }

    // Get user by id
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Check if user who sent request is authenticated by checking if a user with the given token exists in the
    // repository and if the user is online
    public Boolean checkUserAuthentication(String token) {
        User user = userRepository.findByToken(token);
        if (user != null) {
            if (user.getStatus() == UserStatus.ONLINE) {
                return true;
            }
        }
        return false;
    }

    // Check if user already exists in the repository
    public Boolean checkUserExists(String username) {
        if (userRepository.findByUsername(username) != null) {
            return true;
        } else {
            return false;
        }
    }

    // Login user by first checking if user exists, then check if password is correct
    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                user.setStatus(UserStatus.ONLINE);
                userRepository.save(user);
                return user;
            }
        }
        return null;
    }

    // Logout user by token
    public Boolean logoutUser(String token) {
        User user = userRepository.findByToken(token);
        if (user != null) {
            user.setStatus(UserStatus.OFFLINE);
            return true;
        }
        return false;
    }

    // Update user by overriding existing user data
    public void updateUser(User currentUser, User updatedUser) {
        if (updatedUser.getUsername() != null) {
            currentUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getBirthdayDate() != null) {
            currentUser.setBirthdayDate(updatedUser.getBirthdayDate());
        }
        userRepository.save(currentUser);
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.OFFLINE);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }
}
