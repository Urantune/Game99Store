package WebBackEnd.repository;

import WebBackEnd.Entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
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

    Game findGameByGameId(UUID gameId);

    List<Game> findByGameCategoryIgnoreCase(String category);

}
