package service;

import model.CartItem;
import model.Product;
import org.springframework.stereotype.Service;
import repositories.CartRepository;
import repositories.ProductRepository;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> getCartByUserId(Integer userId) {
        return cartRepository.findByUserId(userId);
    }

    public boolean addToCart(Integer userId, Integer productId, int quantity) {
        // đã có trong giỏ
        if (cartRepository.existsByUserIdAndProductId(userId, productId)) {
            return false;
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return false; // sản phẩm không tồn tại
        }

        CartItem item = new CartItem(
                userId,
                product.getId(),
                product.getName(),
                product.getPrice(),
                quantity
        );
        cartRepository.save(item);
        return true;
    }

    public boolean removeFromCart(Integer userId, Integer productId) {
        CartItem item = cartRepository.findByUserIdAndProductId(userId, productId);
        if (item != null) {
            cartRepository.delete(item);
            return true;
        }
        return false;
    }

    // Nếu cần hàm check riêng, đặt trong class
    public boolean existsInDB(Integer userId, Integer productId) {
        return cartRepository.existsByUserIdAndProductId(userId, productId);
    }
}
