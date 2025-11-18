package webbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webbackend.entity.User;
import webbackend.entity.VoucherUser;
import webbackend.entity.Vouncher;

import java.util.UUID;

@Repository
public interface VoucherUserRepository extends JpaRepository<VoucherUser, UUID> {

    VoucherUser findByVouncherAndUser(Vouncher vouncher, User user);
}
