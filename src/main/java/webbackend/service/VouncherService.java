package webbackend.service;

import webbackend.entity.Vouncher;
import webbackend.repository.VouncherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VouncherService {

    @Autowired
    private VouncherRepository vouncherRepository;


    public List<Vouncher> findAll() {
        return vouncherRepository.findAll();
    }


    public void save(Vouncher vouncher) {
        vouncherRepository.save(vouncher);
    }

    public Vouncher findByUuid(UUID uuid) {
        return vouncherRepository.findByVoucherid(uuid);
    }

    public void deleteById(UUID id) {
        vouncherRepository.deleteById(id);
    }

    public Vouncher findByName(String name) {
        return vouncherRepository.findByName(name);
    }

}
