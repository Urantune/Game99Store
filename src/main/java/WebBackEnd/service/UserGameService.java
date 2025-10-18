package WebBackEnd.service;


import WebBackEnd.model.Entity.UserGame;
import WebBackEnd.repository.UserGameRepository;
import org.springframework.stereotype.Service;

@Service
public class UserGameService {

    UserGameRepository userGameRepository;


    public void save(UserGame userGame){
        userGameRepository.save(userGame);
    }
}
