package webbackend.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webbackend.entity.UserGameArchive;
import webbackend.repository.UserGameArchiveRepository;

@Service
public class UserGameArchiveService {

    @Autowired
    private UserGameArchiveRepository userGameArchiveRepository;

    public void save(UserGameArchive userGameArchive){
        userGameArchiveRepository.save(userGameArchive);
    }


}
