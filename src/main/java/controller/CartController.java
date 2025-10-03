package controller;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;
import model.CartItem;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class CartController {
    @Controller
    @RequestMapping("/cart")
    public class CartController {


        private final CartService cartService;

        public CartController(CartService cartService) {
            this.cartService = cartService;
        }

        // Hiển thị giỏ hàng (GET)
        @GetMapping
        public String showCart(HttpSession session, Model model) {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return "redirect:/Index"; // chưa đăng nhập
            }

            List<CartItem> cart = cartService.getCartByUserId(user.getUserId());
            session.setAttribute("cart", cart);
            return "Cart"; // -> Cart.jsp hoặc Cart.html
        }

        // Thêm hoặc xóa sản phẩm (POST)
        @PostMapping
        public String updateCart(@RequestParam("action") String action,
                                 @RequestParam("productId") Integer productId,
                                 HttpSession session,
                                 Model model) {

            User user = (User) session.getAttribute("user");
            if (user == null) {
                model.addAttribute("cartError", "Bạn phải đăng nhập để quản lý giỏ hàng.");
                return "Index";
            }

            boolean success;
            if ("remove".equalsIgnoreCase(action)) {
                success = cartService.removeFromCart(user.getUserId(), productId);
                if (!success) {
                    model.addAttribute("cartError", "Không thể xóa sản phẩm!");
                    return "Index";
                }
            } else { // add
                success = cartService.addToCart(user.getUserId(), productId, 1);
                if (!success) {
                    model.addAttribute("cartError", "Sản phẩm đã tồn tại hoặc không hợp lệ!");
                    return "Index";
                }
            }

            // Cập nhật lại session sau khi thao tác
            List<CartItem> updatedCart = cartService.getCartByUserId(user.getUserId());
            session.setAttribute("cart", updatedCart);

            return "redirect:/cart"; // load lại giỏ hàng
        }
    }
}
