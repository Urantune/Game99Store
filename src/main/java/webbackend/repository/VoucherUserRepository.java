package webbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webbackend.entity.Users;
import webbackend.entity.VoucherUser;
import webbackend.entity.Vouncher;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoucherUserRepository extends JpaRepository<VoucherUser, UUID> {

    VoucherUser findByVouncherAndUser(Vouncher vouncher, Users user);

    List<VoucherUser> findByVouncher(Vouncher vouncher);

    void deleteByVouncher(Vouncher vouncher);
}
