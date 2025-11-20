package webbackend.service;


import webbackend.entity.Admin;
import webbackend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AdminSevice {

    @Autowired
    private AdminRepository adminRepository;

    public List<Admin> findAllAdmin(){
        return adminRepository.findAll();
    }

    public Admin findByUsername(String username) {
        return adminRepository.findByAdminName(username);
    }

    public Admin findByAdminid(UUID adminid) {
        return  adminRepository.findById(adminid).orElse(null);
    }


    public List<Admin> findByRole(String role) {
        return adminRepository.findByRole(role);
    }

    public void save(Admin admin) {
        adminRepository.save(admin);
    }

}
