package webbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webbackend.entity.UserGameArchive;

import java.util.UUID;

@Repository
public interface UserGameArchiveRepository extends JpaRepository<UserGameArchive, UUID> {
}
