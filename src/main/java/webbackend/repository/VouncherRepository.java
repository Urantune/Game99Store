package webbackend.repository;

import webbackend.entity.Vouncher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface VouncherRepository extends JpaRepository<Vouncher, UUID> {

    Vouncher findByVoucherid(UUID voucherid);

    Vouncher findByName(String name);
}
