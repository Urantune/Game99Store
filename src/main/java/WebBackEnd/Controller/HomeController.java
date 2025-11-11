package WebBackEnd.Controller;

import WebBackEnd.Entity.*;
import WebBackEnd.SucDat.GameCore;
import WebBackEnd.SucDat.SendMailTest;
import WebBackEnd.repository.UserGameRepository;
import WebBackEnd.repository.UserRepository;
import WebBackEnd.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping(value = "/welcome")
public class HomeController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private GameSevice gameSevice;
    @Autowired
    private UserGameService userGameService;
    @Autowired
    private SendMailTest sendMailTest;
    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private GameCore gameCore;
    @Autowired
    private UserGameRepository userGameRepository;
    @Autowired
    private EventService eventService;
    @Autowired
    private WebBackEnd.Service.UserTransactionService transactionService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VouncherService vouncherService;

    @GetMapping
    public String homepage(Model model, HttpSession session) {
        if (!model.containsAttribute("showForm")) {
            model.addAttribute("showForm", "");
        }
        model.addAttribute("gameMain", gameSevice.findGameByStatus("main"));
        model.addAttribute("listGame", gameSevice.list20GameIntoGame());
        model.addAttribute("linkimage", GameCore.imageLinkGame(gameSevice.findGameByStatus("main").getImageLinks()));

        UUID userId = (UUID) session.getAttribute("userId");
        User user = null;
        if (userId != null) {
            user = userService.getUserById(userId);
        }
        model.addAttribute("user", user);

        return "HTML/Index";
    }

    @GetMapping("/news")
    public String news(Model model) {
        return "HTML/news";
    }

    @GetMapping("/test")
    public String homepage2(Model model) {
        return "HTML/seat.html";
    }

    @GetMapping("/Cart/{id}")
    public String payMent(@PathVariable UUID id, Model model) {
        model.addAttribute("listGame", userGameService.showGameInCart(id));
        model.addAttribute("user", userService.findById(id));
        return "HTML/Cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("gameId") UUID gameId,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) return "redirect:/welcome/login";

        User user = userService.findById(userId);
        Game game = gameSevice.findGameById(gameId);
        if (user == null || game == null) {
            ra.addFlashAttribute("message", "Yêu cầu không hợp lệ.");
            return "redirect:/welcome/Cart/" + userId;
        }

        UserGame ug = userGameService.findByGameAndUser(game, user);
        if (ug != null && ug.getStatus() == 0) {
            userGameRepository.delete(ug);
            ra.addFlashAttribute("cartSuccess", "Đã xóa khỏi giỏ hàng");
        } else {
            ra.addFlashAttribute("cartError", "Không thể xóa mục này");
        }

        return "redirect:/welcome/Cart/" + userId;
    }

    @PostMapping("/buy")
    public String buy(@RequestParam(value = "selectedIds", required = false) List<UUID> selectedIds,
                      Model model,
                      HttpSession session,
                      RedirectAttributes ra) {
        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) return "redirect:/welcome/login";

        if (selectedIds == null || selectedIds.isEmpty()) {
            ra.addFlashAttribute("message", "Vui lòng chọn ít nhất 1 game.");
            return "redirect:/welcome/Cart/" + userId;
        }

        List<Game> valid = new ArrayList<>();
        for (UUID id : selectedIds) {
            Game g = gameSevice.findGameById(id);
            if (g != null) valid.add(g);
        }
        if (valid.isEmpty()) {
            ra.addFlashAttribute("message", "Không tìm thấy game hợp lệ để thanh toán.");
            return "redirect:/welcome/Cart/" + userId;
        }

        session.setAttribute("checkoutSelectedIds", selectedIds);
        model.addAttribute("listGame", valid);
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("vouchers", vouncherService.findAll());
        return "HTML/Buy";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam(value = "selectedIds", required = false) List<UUID> selectedIds,
                           @RequestParam("userId") UUID userIdFromForm,
                           HttpSession session,
                           Model model,
                           RedirectAttributes ra) {
        UUID sessionUserId = (UUID) session.getAttribute("userId");
        if (sessionUserId == null) return "redirect:/welcome/login";
        if (!sessionUserId.equals(userIdFromForm)) {
            ra.addFlashAttribute("message", "Phiên không hợp lệ. Vui lòng thử lại.");
            return "redirect:/welcome/Cart/" + sessionUserId;
        }

        if (selectedIds == null || selectedIds.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<UUID> idsInSession = (List<UUID>) session.getAttribute("checkoutSelectedIds");
            if (idsInSession == null || idsInSession.isEmpty()) {
                ra.addFlashAttribute("message", "Vui lòng chọn game để thanh toán.");
                return "redirect:/welcome/Cart/" + sessionUserId;
            }
            selectedIds = idsInSession;
        }

        User user = userService.findById(sessionUserId);
        if (user == null) return "redirect:/welcome/login";

        List<Game> payable = new ArrayList<>();
        for (UUID id : selectedIds) {
            Game g = gameSevice.findGameById(id);
            if (g != null) payable.add(g);
        }
        if (payable.isEmpty()) {
            ra.addFlashAttribute("message", "Không có game hợp lệ để thanh toán.");
            return "redirect:/welcome/Cart/" + sessionUserId;
        }

        double total = 0d;
        for (Game g : payable) {
            total += (g.getPrice() > 0 ? g.getPrice() : 0d);
        }

        if (user.getPrice() < total) {
            model.addAttribute("thongbao", "khongdusodu");
            model.addAttribute("listGame", payable);
            model.addAttribute("user", user);
            return "HTML/Buy";
        }

        user.setPrice(user.getPrice() - total);
        userService.save(user);

        for (Game g : payable) {
            UserGame ug = userGameService.findByGameAndUser(g, user);
            if (ug == null) {
                ug = new UserGame(user, g, LocalDateTime.now(), 1);
            } else {
                ug.setStatus(1);
                ug.setPurchaseDate(LocalDateTime.now());
            }
            userGameService.saveUserGame(ug);
        }

        session.removeAttribute("checkoutSelectedIds");
        ra.addFlashAttribute("message", "Thanh toán thành công!");
        return "redirect:/welcome/Cart/" + sessionUserId;
    }

    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> registerAjax(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        String username = user.getUsername();
        String email = user.getEmail();
        String rawPassword = user.getPassword();

        if (username == null || username.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản không được để trống");
            return response;
        }
        if (!username.matches("^[a-zA-Z0-9._]+$")) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản chỉ được chứa chữ, số, dấu chấm hoặc gạch dưới");
            return response;
        }
        if (username.startsWith(".") || username.startsWith("_") ||
                username.endsWith(".") || username.endsWith("_")) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản không được bắt đầu hoặc kết thúc bằng dấu chấm hoặc gạch dưới");
            return response;
        }
        if (username.contains("..") || username.contains("__") ||
                username.contains("._") || username.contains("_.")) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản không được chứa ký tự đặc biệt liên tiếp");
            return response;
        }
        if (username.length() < 3 || username.length() > 20) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản phải có độ dài từ 3 đến 20 ký tự");
            return response;
        }
        if (userRepository.existsByUsername(username)) {
            response.put("status", "error");
            response.put("message", "Tên tài khoản đã tồn tại");
            return response;
        }
        if (email == null || email.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Email không được để trống");
            return response;
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            response.put("status", "error");
            response.put("message", "Mật khẩu không được để trống");
            return response;
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setScore(0);
        user.setStatus("wait");
        user.setDateCreateAccount(LocalDateTime.now());
        userRepository.save(user);

        String input = "wait" + user.getId();
        String fi;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            fi = sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String title = "Xác nhận tài khoản của bạn";
        String link = "https://a7c804c1ed63.ngrok-free.app/veryAccount/done/" + user.getId() + "/" + fi;
        String content =
                "<p>Hãy nhấp vào liên kết dưới đây để kích hoạt tài khoản của bạn:</p>"
                        + "<p><a href=\"" + link + "\">Nhấn vào đây để kích hoạt</a></p>"
                        + "<p>Nếu không bấm được, copy link sau dán vào trình duyệt:<br>" + link + "</p>";
        sendMailTest.testSend(user.getEmail(), title, content);

        response.put("status", "success");
        response.put("message", "Đăng ký thành công! Một đường link xác thực tài khoản đã được gửi vào email của bạn.");
        return response;
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password,
                                   HttpSession session) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tài khoản không tồn tại!"));
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Sai mật khẩu!"));
        }
        if ("wait".equalsIgnoreCase(user.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tài khoản chưa được kích hoạt"));
        }
        if ("banned".equalsIgnoreCase(user.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tài khoảng của bạn đã bị cấm"));
        }

        session.setAttribute("user", user);
        session.setAttribute("id", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("userId", user.getId());
        session.setAttribute("userUsername", user.getUsername());

        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/about")
    public String controllAbout(Model model) {
        return "HTML/About";
    }

    @GetMapping("/refundGame")
    public String refundGame() {
        return "HTML/RefundGame";
    }

    @GetMapping("/buyguide")
    public String buyguide() {
        return "HTML/BuyGuide";
    }

    @GetMapping("/category/{product}")
    public String category(@PathVariable("product") String product, Model model) {
        List<Game> games = gameSevice.findGamesByCetagory(product);
        model.addAttribute("listGame", games);
        model.addAttribute("currentCategory", product);
        model.addAttribute("tieude", product);
        return "HTML/Category";
    }

    @GetMapping("/gamedetail/{game_id}")
    public String gameDetail(@PathVariable("game_id") UUID game_id,
                             Model model,
                             HttpSession session) {
        Game game = gameSevice.findGameById(game_id);
        model.addAttribute("game", game);

        UUID userId = (UUID) session.getAttribute("userId");
        boolean userGame = false;
        boolean canFeedbackAndDownload = false;
        Feedback myFeedback = null;

        List<Feedback> list = new ArrayList<>(feedbackService.findFeedbackByGameId(game_id));

        if (userId != null) {
            User user = userService.findById(userId);
            for (Iterator<Feedback> it = list.iterator(); it.hasNext(); ) {
                Feedback f = it.next();
                if (f.getUserId().equals(userId)) {
                    myFeedback = f;
                    it.remove();
                    break;
                }
            }
            UserGame ug = userGameService.findByGameAndUser(game, user);
            userGame = (ug != null);
            canFeedbackAndDownload = (ug != null && ug.getStatus() == 1);
        }

        model.addAttribute("UserGame", userGame);
        model.addAttribute("canFeedbackandDownload", canFeedbackAndDownload);
        model.addAttribute("listFeedback", list);
        model.addAttribute("myFeedback", myFeedback);

        return "HTML/GameDetail";
    }

    @PostMapping("/gamedetail/{game_id}")
    public String saveFeedback(@PathVariable("game_id") UUID gameId,
                               @RequestParam("star") Double star,
                               @RequestParam("comment") String cmt,
                               HttpSession session) {
        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) return "redirect:/welcome/login";

        List<Feedback> lst = feedbackService.findFeedbackByGameId(gameId);
        Feedback my = lst.stream()
                .filter(f -> f.getUserId().equals(userId))
                .findFirst().orElse(null);

        if (my == null) {
            my = new Feedback(gameId, userId, cmt, star);
        } else {
            my.setStar(star);
            my.setComment(cmt);
        }
        feedbackService.saveFeedback(my);

        return "redirect:/welcome/gamedetail/" + gameId;
    }

    @GetMapping("/BuyGuide")
    public String buyGuide(Model model) {
        return "HTML/BuyGuide";
    }

    @GetMapping("/Newgame")
    public String newgame(Model model) {
        model.addAttribute("gameCore", gameCore);
        model.addAttribute("eventMain", eventService.findEventByType("event_main"));
        model.addAttribute("eventNext", eventService.findEventByType("event_next"));
        model.addAttribute("events", eventService.findEventsByType("event_small"));
        return "HTML/NewGame";
    }

    @GetMapping("/privacypolicy")
    public String privacypolicy() {
        return "HTML/PrivacyPolicy";
    }

    @GetMapping("/support")
    public String support(Model model) {
        return "HTML/Support";
    }

    @GetMapping("/supporttransaction")
    public String supporttransaction(Model model) {
        return "HTML/SupportTransaction";
    }

    @GetMapping("/termsofservice")
    public String termsofservice() {
        return "HTML/TermsOfService";
    }

    @GetMapping("/profile/{id}")
    public String userDetail(@PathVariable UUID id, Model model,HttpSession session) {
        User user = (User)  session.getAttribute("user");
        if (user == null) return "redirect:/welcome";
        model.addAttribute("user", user);
        model.addAttribute("id", id);
        model.addAttribute("listGame", userGameService.showGameInProfile(id));
        return "HTML/ProfileUser";
    }

    @PostMapping("/addGameToCard/{game_id}")
    public String addGameToCard(@PathVariable("game_id") UUID gameId,
                                HttpSession session,
                                RedirectAttributes ra) {
        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/welcome/login";
        }

        var user = userService.findById(userId);
        var game = gameSevice.findGameById(gameId);

        boolean existed = userGameService.findUserGameByUserAndGame(user, game);

        if (existed) {
            ra.addAttribute("cartError",
                    java.net.URLEncoder.encode("Bạn đã thêm game này vào giỏ hàng rồi", java.nio.charset.StandardCharsets.UTF_8));
        } else {
            UserGame ug = new UserGame(user, game, java.time.LocalDateTime.now(), 0);
            userGameService.saveUserGame(ug);
            ra.addAttribute("cartSuccess",
                    java.net.URLEncoder.encode("Đã thêm vào giỏ hàng!", java.nio.charset.StandardCharsets.UTF_8));
        }

        return "redirect:/welcome/gamedetail/{game_id}";
    }

    @GetMapping("/editprofile/{id}")
    public String editProfile(Model model, @PathVariable(value = "id") UUID id) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "HTML/EditProfile";
    }

    @Controller
    public class AuthController {
        @GetMapping("/logout")
        public String logout(HttpServletRequest request) {
            request.getSession().invalidate();
            return "redirect:/welcome";
        }
    }

    @GetMapping("/transactions")
    public String viewTransactions(HttpSession session, Model model) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return "redirect:/welcome";
        }
        User currentUser = userService.findById((java.util.UUID) userIdObj);
        model.addAttribute("user", currentUser);
        model.addAttribute("topups", transactionService.getTopups(currentUser));
        model.addAttribute("purchases", userGameService.getGamesByUser(currentUser));
        model.addAttribute("refunds", transactionService.getRefunds(currentUser));
        return "HTML/TransactionHistory";
    }

    @GetMapping("/gamePay")
    public String gamePay(Model model) {
        return "HTML/GamePay";
    }

    @GetMapping("/game/detail/{id}")
    public String gameDetail(@PathVariable UUID id, Model model) {
        Game game = gameSevice.findGameById(id);
        model.addAttribute("game", game);
        return "HTML/GameDetail";
    }

    @PostMapping("/verify-reset")
    @ResponseBody
    public ResponseEntity<?> verifyEmailForReset(@RequestBody Map<String, String> payload) {
        try {
            String idStr = payload.get("id");
            String email = payload.get("email");

            if (idStr == null || email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu tham số id/email"));
            }

            UUID id = UUID.fromString(idStr);
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Không tìm thấy người dùng"));
            }

            if (!user.getEmail().equalsIgnoreCase(email)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email không trùng với email tài khoản"));
            }

            return ResponseEntity.ok(Map.of("message", "Xác thực email thành công"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Yêu cầu không hợp lệ"));
        }
    }

    @GetMapping("/changepass")
    public String changePass(Model model, HttpSession session) {
        User user = userService.findById(UUID.fromString("6CE0FCF6-B584-4A63-AEDF-FAED48E78665"));
        user.setStatus("changePass");
        userService.save(user);

        String input = "wait" + user.getId();
        String fi;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            fi = sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String title = "Đổi mật khẩu của bạn";
        String link = "http://localhost:8080/veryAccount/donePass/" + user.getId() + "/" + fi;

        String content =
                "<p>Hãy nhấp vào liên kết dưới đây để thay đổi mật khẩu của bạn:</p>"
                        + "<p><a href=\"" + link + "\">Nhấn vào đây để đổi</a></p>"
                        + "<p>Nếu không bấm được, copy link sau dán vào trình duyệt:<br>" + link + "</p>";
        sendMailTest.testSend(user.getEmail(), title, content);

        return "HTML/SendPassDone";
    }

    @GetMapping("/games/all")
    @ResponseBody
    public List<Map<String, Object>> getAllGames() {
        return gameSevice.findAllGame().stream().map(game -> {
            Map<String, Object> g = new HashMap<>();
            g.put("id", game.getGameId());
            g.put("name", game.getGameName());
            String[] imgs = game.getLinkImage();
            String mainImage = "/img/notfound.png";
            if (imgs != null && imgs.length > 0) {
                for (String link : imgs) {
                    String lower = link.toLowerCase();
                    if (lower.contains("img/game") && lower.endsWith(".jpg")) {
                        mainImage = "/" + link;
                        break;
                    }
                }
                if (mainImage.equals("/img/notfound.png")) {
                    for (String link : imgs) {
                        if (!link.endsWith(".mp4")) {
                            mainImage = "/" + link;
                            break;
                        }
                    }
                }
            }
            g.put("image", mainImage);
            return g;
        }).toList();
    }
}
