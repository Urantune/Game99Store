package controller;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.OrderService;

@Controller
public class BuyController {

    private final OrderService orderService;

    public BuyController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/buy")
    public String buyProduct(@RequestParam("productId") Integer productId,
                             HttpSession session,
                             Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("buyError", "Bạn phải đăng nhập để mua hàng!");
            return "Index";
        }

        boolean success = orderService.buyProduct(user.getId(), productId);

        if (success) {
            return "success"; // trang success.jsp / success.html
        } else {
            model.addAttribute("buyError", "Không thể xử lý đơn hàng. Vui lòng thử lại!");
            return "Index";
        }
    }
}