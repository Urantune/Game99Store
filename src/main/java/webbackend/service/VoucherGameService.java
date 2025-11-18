package webbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webbackend.entity.Game;
import webbackend.entity.VoucherGame;
import webbackend.entity.Vouncher;
import webbackend.repository.VoucherGameRepository;

import java.util.List;

@Service
public class VoucherGameService {

    @Autowired
    private VoucherGameRepository voucherGameRepository;

   public List<VoucherGame> getVoucherGamesByGame(Game game) {
        return voucherGameRepository.findByGame(game);
    }

}
