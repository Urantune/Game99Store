package webbackend.service;

import webbackend.entity.Users;
import webbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public Users saveUser(Users user) {
        return userRepository.save(user);
    }

    public List<Users> findAll() {
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

    public Users findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    public Users save(Users user) {
        return userRepository.save(user);
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    public Users findById(UUID id) {
        return userRepository.findUserById(id);
    }

    public Users findUserByStatus(String status) {
        return userRepository.findByStatus(status);
    }

    public Users getUserById(UUID id){
        return userRepository.findUserById(id);
    }
}
