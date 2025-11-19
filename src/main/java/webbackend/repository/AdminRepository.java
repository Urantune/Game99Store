package webbackend.repository;

import webbackend.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {
        Admin findByAdminName(String adminName);

    Admin findByAdminid(UUID adminid);
}
