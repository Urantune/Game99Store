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
        return userGameRepository.findGamesByUserAndStatus(userId,0);
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

    // Lấy danh sách game thành công
    public List<UserGame> getCompletedGames(User user) {
        return userGameRepository.findByUserAndStatus(user, 1);
    }

    // Lấy danh sách game đang xử lý
    public List<UserGame> getPendingGames(User user) {
        return userGameRepository.findByUserAndStatus(user, 0);
    }

    
}
