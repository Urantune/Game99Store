package webbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webbackend.entity.Users;
import webbackend.entity.VoucherUser;
import webbackend.entity.Vouncher;
import webbackend.repository.VoucherUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VoucherUserService {

    @Autowired
    private VoucherUserRepository voucherUserRepository;
    @Autowired
    private UserService userService;

    public VoucherUser getVoucherUserByVouncherAndUser(Vouncher vouncher, Users user) {
        return voucherUserRepository.findByVouncherAndUser(vouncher, user);
    }

    public List<VoucherUser> getVoucherUserByVouncher(Vouncher vouncher) {
        return voucherUserRepository.findByVouncher(vouncher);
    }




    @Transactional
    public void updateUsersForVoucher(Vouncher vouncher, List<UUID> userIds) {
        if (vouncher == null || vouncher.getVoucherid() == null) return;


        voucherUserRepository.deleteByVouncher(vouncher);


        if (userIds == null || userIds.isEmpty()) return;


        for (UUID uid : userIds) {
            Users u = userService.findById(uid);
            if (u == null) continue;

            VoucherUser vu = new VoucherUser(u, vouncher, LocalDateTime.now());
            voucherUserRepository.save(vu);
        }
    }
}
