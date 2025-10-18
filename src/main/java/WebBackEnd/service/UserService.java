package WebBackEnd.service;

import WebBackEnd.model.Entity.User;
import WebBackEnd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }


    public boolean existsByEmail(String email) {
        return userRepository.findAll()
                .stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    public boolean existsByUsername(String username) {
        return userRepository.findAll()
                .stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    public User findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }
}
