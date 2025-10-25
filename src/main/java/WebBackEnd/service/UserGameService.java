package WebBackEnd.service;


import WebBackEnd.Entity.Game;
import WebBackEnd.Entity.User;
import WebBackEnd.Entity.UserGame;
import WebBackEnd.repository.UserGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserGameService {

    @Autowired
    UserGameRepository userGameRepository;


    public void save(UserGame userGame){
        userGameRepository.save(userGame);
    }

    public List<Game> showGameInProfile(UUID userId){
        return userGameRepository.findGamesOwnedByUser(userId);
    }

    public List<Game> showGameInCart(UUID userId){
        return userGameRepository.findGamesByUserAndStatus(userId,0);
    }



    public boolean findUserGameByUserAndGame(User user, Game game){
        return userGameRepository.existsUserGameByUserAndGame(user,game);
    }

    public void saveUserGame(UserGame userGame){
        userGameRepository.save(userGame);
    }


    
}
