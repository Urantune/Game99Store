package webbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webbackend.entity.Game;
import webbackend.entity.VoucherGame;
import webbackend.entity.Vouncher;
import webbackend.repository.VoucherGameRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VoucherGameService {

    @Autowired
    private VoucherGameRepository voucherGameRepository;


    @Autowired
    private GameSevice gameSevice;

   public List<VoucherGame> getVoucherGamesByGame(Game game) {
        return voucherGameRepository.findByGame(game);
    }

    public List<VoucherGame> getVoucherGameByVouncher(Vouncher vouncher) {
        return voucherGameRepository.findByVouncher(vouncher);
    }


    @Transactional
    public void updateGamesForVoucher(Vouncher vouncher, List<UUID> gameIds) {
        if (vouncher == null || vouncher.getVoucherid() == null) return;


        voucherGameRepository.deleteByVouncher(vouncher);


        if (gameIds == null || gameIds.isEmpty()) return;

        for (UUID gid : gameIds) {
            Game g = gameSevice.findById(gid);
            if (g == null) continue;

            VoucherGame vg = new VoucherGame(g, vouncher, LocalDateTime.now());
            voucherGameRepository.save(vg);
        }
    }



}
