package WebBackEnd.repository;

import WebBackEnd.Entity.Vouncher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface VouncherRepository extends JpaRepository<Vouncher, UUID> {

    Vouncher findByVoucherId(UUID uuid);
}
