package WebBackEnd.repository;

import WebBackEnd.model.Entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {

    public List<Game> findByGameId(String username);
}
