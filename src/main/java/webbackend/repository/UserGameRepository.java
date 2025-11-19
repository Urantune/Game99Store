package webbackend.repository;

import webbackend.entity.Game;
import webbackend.entity.User;
import webbackend.entity.UserGame;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
@Repository
public interface UserGameRepository extends JpaRepository<UserGame, UUID> {

    List<UserGame> findByUser(User user);
    List<UserGame> findByUserAndStatus(User user, String status);
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
       where ug.user.id = ?1
       order by ug.purchaseDate desc
       """)
    List<Game> findGamesOwnedByUser(UUID userId);

    @Query("""
       select ug.game
       from UserGame ug
       where ug.user.id = ?1
         and ug.status = 'cart'
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
                                        @Param("status") String status);

        boolean existsUserGameByUserAndGame(User user, Game game);

        UserGame findByGameAndUser(Game game, User user);




    @Modifying
    @Transactional
        void deleteAllByUser(User user);

    @Modifying
    @Transactional
    void deleteAllByGame(Game game);


    @Modifying
    @Transactional
    void deleteByUserAndGame(User user, Game game);

    List<UserGame> findByUserAndPurchaseDateAndStatus(
            User user,
            LocalDateTime purchaseDate,
            String status
    );


    List<UserGame> findByUserAndStatusIn(User user, Collection<String> status);


    List<UserGame> findByStatusOrderByPurchaseDateDesc(String status);


}
