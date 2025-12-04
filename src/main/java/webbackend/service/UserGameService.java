package webbackend.service;


import webbackend.entity.Game;
import webbackend.entity.Users;
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



    public boolean findUserGameByUserAndGame(Users user, Game game){
        return userGameRepository.existsUserGameByUserAndGame(user,game);
    }

    public void saveUserGame(UserGame userGame){
        userGameRepository.save(userGame);
    }

    public List<UserGame> getGamesByUser(Users user) {
        return userGameRepository.findByUser(user);
    }


    public List<UserGame> getCompletedGames(Users user) {
        return userGameRepository.findByUserAndStatus(user, "owned");
    }


    public List<UserGame> getPendingGames(Users user) {
        return userGameRepository.findByUserAndStatus(user, "cart");
    }

    public List<UserGame> findAll(){
        return userGameRepository.findAll();
    }

    public UserGame findByGameAndUser(Game game, Users user){
        return userGameRepository.findByGameAndUser(game,user);
    }


    public void DeleteByUser(Users user){
        userGameRepository.deleteAllByUser(user);
    }

    public void DeleteByGame(Game game){
        userGameRepository.deleteAllByGame(game);
    }

    public void DeleteByUserGame(Users user, Game game){
        userGameRepository.deleteByUserAndGame(user,game);
    }


}
