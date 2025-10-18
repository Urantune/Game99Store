package WebBackEnd.repository;

import WebBackEnd.model.Entity.Game;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {


    public Game findGameByStatus(String status);


    @Query(value = """
                SELECT TOP 20 *
                FROM games
                WHERE status <> 'main'
                ORDER BY NEWID()
            """, nativeQuery = true)
    List<Game> show20GameRandom();


    Optional<Game> findByGameId(UUID gameId);



}
