package webbackend.service;


import webbackend.entity.Game;
import webbackend.entity.User;
import webbackend.entity.UserGame;
import webbackend.repository.UserGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserGameService {

    @Autowired
    UserGameRepository userGameRepository;
    public UserGameService(UserGameRepository userGameRepository) {
        this.userGameRepository = userGameRepository;
    }

    public void save(UserGame userGame){
        userGameRepository.save(userGame);
    }

    public List<Game> showGameInProfile(UUID userId){
        return userGameRepository.findGamesOwnedByUser(userId);
    }

    public List<Game> showGameInCart(UUID userId){
        return userGameRepository.findGamesByUserAndStatus(userId,"cart");
    }



    public boolean findUserGameByUserAndGame(User user, Game game){
        return userGameRepository.existsUserGameByUserAndGame(user,game);
    }

    public void saveUserGame(UserGame userGame){
        userGameRepository.save(userGame);
    }

    public List<UserGame> getGamesByUser(User user) {
        return userGameRepository.findByUser(user);
    }


    public List<UserGame> getCompletedGames(User user) {
        return userGameRepository.findByUserAndStatus(user, "owned");
    }


    public List<UserGame> getPendingGames(User user) {
        return userGameRepository.findByUserAndStatus(user, "cart");
    }

    public UserGame findByGameAndUser(Game game, User user){
        return userGameRepository.findByGameAndUser(game,user);
    }


    public void DeleteByUser(User user){
        userGameRepository.deleteAllByUser(user);
    }

    public void DeleteByGame(Game game){
        userGameRepository.deleteAllByGame(game);
    }

    public void DeleteByUserGame(User user, Game game){
        userGameRepository.deleteByUserAndGame(user,game);
    }


}
