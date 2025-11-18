package webbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webbackend.entity.ImageGame;
import webbackend.repository.ImageGameRepository;

import java.util.UUID;

@Service
public class ImageGameService {

    @Autowired
    private ImageGameRepository imageGameRepository;

    public ImageGame findByGameId(UUID gameId) {
        return imageGameRepository.findByGame_GameId(gameId);
    }

    public void save(ImageGame imageGame) {
        imageGameRepository.save(imageGame);
    }
}
