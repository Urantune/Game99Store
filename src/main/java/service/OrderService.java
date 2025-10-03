package service;

import model.Order;
import model.Product;
import org.springframework.stereotype.Service;
import repositories.OrderRepository;
import repositories.ProductRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public boolean buyProduct(Integer userId, Integer productId) {
        try {
            // Lấy thông tin sản phẩm
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) {
                return false;
            }

            // Tạo đơn hàng (mặc định quantity = 1)
            Order order = new Order(
                    userId,
                    productId,
                    1,
                    product.getPrice()
            );

            orderRepository.save(order);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
