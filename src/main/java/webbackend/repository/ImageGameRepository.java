package webbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webbackend.entity.ImageGame;

import java.util.List;
import java.util.UUID;
@Repository
public interface ImageGameRepository extends JpaRepository<ImageGame, UUID> {
    ImageGame findByGame_GameId(UUID gameId);

}
