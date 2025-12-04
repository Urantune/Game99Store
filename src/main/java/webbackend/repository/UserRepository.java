package webbackend.repository;

import webbackend.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    public Users findByUsername(String username);

    Users findUserById(UUID id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Users findByStatus(String status);

    List<Users> findByStatusContaining(String keyword);


}
