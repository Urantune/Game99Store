package WebBackEnd.service;


import WebBackEnd.model.Entity.Admin;
import WebBackEnd.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminSevice {

    @Autowired
    private AdminRepository adminRepository;

    public List<Admin> findAllAdmin(){
        return adminRepository.findAll();
    }

}
