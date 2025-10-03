package controller;


import jakarta.servlet.http.HttpSession;
import model.CartItem;
import model.Product;
import model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.CartService;
import service.ProductService;

import java.util.List;

@Controller
public class AddtoCartController {
//Tro Li
    private final CartService cartService;
    private final ProductService productService;

    public AddtoCartController(CartService cartService, ProductService productService) {
        this.cartService = cartService;
        this.productService = productService;
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("productId") Integer productId,
                            HttpSession session,
                            RedirectAttributes redirectAttributes,
                            Model model) {

        // kiểm tra session
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("cartError", "Bạn phải đăng nhập để thêm sản phẩm!");
            return "redirect:/login";
        }

        // kiểm tra sản phẩm
        Product product = productService.getProductById(productId);
        if (product == null) {
            redirectAttributes.addFlashAttribute("cartError", "Sản phẩm không tồn tại!");
            return "redirect:/";
        }

        // session cart
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cartService.existsInSession(cart, productId)) {
            redirectAttributes.addFlashAttribute("cartError", "⚠️ Sản phẩm này đã có trong giỏ hàng!");
            return "redirect:/";
        }

        // thêm vào session
        CartItem newItem = new CartItem(
                user.getUserId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                1
        );
        cart = cartService.addToSessionCart(cart, newItem);
        session.setAttribute("cart", cart);

        // lưu DB
        if (cartService.existsInDB(user.getUserId(), productId)) {
            redirectAttributes.addFlashAttribute("cartError", "⚠️ Sản phẩm này đã có trong giỏ hàng (DB)!");
            return "redirect:/";
        }

        cartService.addToCart(user.getUserId(), productId, 1);

        return "redirect:/cart";
    }
}
