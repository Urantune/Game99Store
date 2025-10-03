package repositories;

import jakarta.transaction.Transactional;
import model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUserId(Integer userId);

    boolean existsByUserIdAndProductId(Integer userId, Integer productId);

    CartItem findByUserIdAndProductId(Integer userId, Integer productId);
}