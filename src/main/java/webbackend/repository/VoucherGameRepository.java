package webbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webbackend.entity.Game;
import webbackend.entity.VoucherGame;
import webbackend.entity.Vouncher;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoucherGameRepository extends JpaRepository<VoucherGame, UUID> {

    List<VoucherGame> findByGame(Game game);

}
