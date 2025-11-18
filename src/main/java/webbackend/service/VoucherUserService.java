package webbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webbackend.entity.User;
import webbackend.entity.VoucherUser;
import webbackend.entity.Vouncher;
import webbackend.repository.VoucherUserRepository;

import java.util.UUID;

@Service
public class VoucherUserService {

    @Autowired
    private VoucherUserRepository voucherUserRepository;

    public VoucherUser getVoucherUserByVouncherAndUser(Vouncher vouncher, User user) {
        return voucherUserRepository.findByVouncherAndUser(vouncher, user);
    }
}
