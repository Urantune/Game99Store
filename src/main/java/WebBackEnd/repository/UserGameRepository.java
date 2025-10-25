package WebBackEnd.repository;

import WebBackEnd.Entity.Game;
import WebBackEnd.Entity.User;
import WebBackEnd.Entity.UserGame;
import WebBackEnd.Entity.UserGameId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface UserGameRepository extends JpaRepository<UserGame, UserGameId> {


    @Query("""
        SELECT ug.game
        FROM UserGame ug
        WHERE ug.game.status <> 'main'
        GROUP BY ug.game
        ORDER BY COUNT(ug.user.id) DESC
    """)
    List<Game> findTopDownloadedGames(Pageable pageable);


    @Query("""
           select ug.game
           from UserGame ug
           where ug.id.userId = ?1
           order by ug.purchaseDate desc
           """)
    List<Game> findGamesOwnedByUser(UUID userId);

        @Query("""
               select ug.game
               from UserGame ug
               where ug.id.userId = ?1 
                              and ug.status = 0
               order by ug.purchaseDate desc
               """)
        List<Game> findGamesCartByUser(UUID userId);


    @Query("""
        select ug.game
        from UserGame ug
        where ug.user.id = :userId
          and ug.status = :status
    """)
    List<Game> findGamesByUserAndStatus(@Param("userId") UUID userId,
                                        @Param("status") int status);

        boolean existsUserGameByUserAndGame(User user, Game game);







}
