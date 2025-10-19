package WebBackEnd.service;


import WebBackEnd.model.Entity.Game;
import WebBackEnd.model.Entity.UserGame;
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
        return userGameRepository.findGamesCartByUser(userId);
    }
}
