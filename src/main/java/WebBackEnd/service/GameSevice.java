package WebBackEnd.service;

import WebBackEnd.model.Entity.Game;
import WebBackEnd.repository.GameRepository;
import WebBackEnd.repository.UserGameRepository;
import WebBackEnd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class GameSevice {


    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private UserGameRepository userGameRepository;



    public Game findGameByStatus(String status){
        return gameRepository.findGameByStatus(status);
    }


    public List<Game> list20GameIntoGame(){
        Pageable top20 = PageRequest.of(0, 20);
//        List<Game> topGames = userGameRepository.findTopDownloadedGames(top20);
        List<Game> list = gameRepository.show20GameRandom();

    return list;
    }


}
