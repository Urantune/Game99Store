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
import java.time.LocalDate;
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
    private WebBackEnd.service.UserTransactionService transactionService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VouncherService vouncherService;

    @GetMapping
    public String homepage(Model model, HttpSession session) {
        if (!model.containsAttribute("showForm")) {
            model.addAttribute("showForm", "");
        }
        model
                .addAttribute("gameMain", gameSevice
                .findGameByStatus("main"));
        model
                .addAttribute("listGame", gameSevice
                .list20GameIntoGame());
        model
                .addAttribute("linkimage", GameCore
                .imageLinkGame(gameSevice
                        .findGameByStatus("main")
                        .getImageLinks()));

        UUID userId = (UUID) session.getAttribute("userId");
        User user = null;
        if (userId != null) {
            user = userService.getUserById(userId);
        }
        model.addAttribute("user", user);

        return "HTML/Index";
    }


    @GetMapping("/Cart/{id}")
    public String payMent(@PathVariable UUID id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/welcome/about";
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





    @GetMapping("/category/{product}")
    public String category(@PathVariable("product") String product, Model model, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("userId");
        User user = null;
        if (userId != null) {
            user = userService.getUserById(userId);
        }
        model.addAttribute("user", user);
        List<Game> games = gameSevice.findGamesByCetagory(product);
        model.addAttribute("listGame", games);
        model.addAttribute("currentCategory", product);
        model.addAttribute("tieude", product);
        return "HTML/Category";
    }



    @PostMapping("/refundGame")
    public String refundGamePost(@RequestParam("gameId") UUID gameId, Model model,
                                 HttpSession session) {

        UUID userId = (UUID) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/welcome/login";
        }

        User user = userService.findById(userId);
        Game game = gameSevice.findGameById(gameId);

        userGameService.DeleteByUserGame(user, game);

        user.setPrice(user.getPrice() + game.getPrice());
        userService.save(user);
        model.addAttribute("gameid", gameId);

        return "HTML/SuccestRefund";
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

    @GetMapping("/succestpayment")
    public String succestory(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false, name = "selectedIds") List<UUID> selectedIds,
            Model model,
            HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/welcome/about";
        }

        UUID goBackGameId = null;

        if (userId != null && selectedIds != null && !selectedIds.isEmpty()) {
            User user = userService.findById(userId);

            for (UUID gameId : selectedIds) {
                Game game = gameSevice.findGameById(gameId);
                UserGame existing = userGameService.findByGameAndUser(game, user);

                if (existing == null) {
                    UserGame newUG = new UserGame();
                    newUG.setId(new UserGameId(user.getId(), game.getGameId()));
                    newUG.setUser(user);
                    newUG.setGame(game);
                    newUG.setStatus(1);
                    newUG.setPurchaseDate(LocalDateTime.now());
                    userGameService.saveUserGame(newUG);
                } else if (existing.getStatus() == 0) {
                    existing.setStatus(1);
                    existing.setPurchaseDate(LocalDateTime.now());
                    userGameService.saveUserGame(existing);
                }
            }


            if (selectedIds.size() == 1) {
                goBackGameId = selectedIds.get(0);
            }
        }


        model.addAttribute("gameid", (goBackGameId != null) ? goBackGameId.toString() : "ok");
        return "HTML/Succest";
    }


    @GetMapping("/Newgame")
    public String newgame(Model model, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("userId");
        User user = null;
        if (userId != null) {
            user = userService.getUserById(userId);
        }
        model.addAttribute("user", user);

        model.addAttribute("gameCore", gameCore);
        model.addAttribute("eventMain", eventService.findEventByType("event_main"));
        model.addAttribute("eventNext", eventService.findEventByType("event_next"));

        var smallEvents = eventService.findEventsByType("event_small");
        model.addAttribute("events", smallEvents != null ? smallEvents : java.util.Collections.emptyList());

        model.addAttribute("games", java.util.Collections.emptyList());
        model.addAttribute("registeredGames", java.util.Collections.emptySet());

        return "HTML/NewGame";
    }

    @GetMapping("/profile/{id}")
    public String userDetail(@PathVariable UUID id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/welcome";

        List<Game> game = userGameService.showGameInProfile(id);
        game.removeIf(g -> {
            UserGame userGame = userGameService.findByGameAndUser(g, user);
            return userGame != null && userGame.getStatus() != 1;
        });

        model.addAttribute("user", user);
        model.addAttribute("id", id);
        model.addAttribute("listGame", game);
        return "HTML/ProfileUser";
    }


    @PostMapping("/profile/{id}/avatar")
    @ResponseBody
    public ResponseEntity<?> updateAvatar(@PathVariable UUID id,
                                          @RequestParam("avatarPath") String avatarPath,
                                          HttpSession session) {

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Bạn chưa đăng nhập"));
        }

        if (!sessionUser.getId().equals(id)) {
            return ResponseEntity.status(403).body(Map.of("error", "Không có quyền đổi avatar của người khác"));
        }


        List<String> allowed = List.of(
                "/img/a.png", "/img/NataliKhang.jpg", "/img/JoLong.jpg",
                "/img/khangbo.jpg", "/img/BiTrong.jpg", "/img/5000.jpg"
        );
        if (!allowed.contains(avatarPath)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Ảnh không hợp lệ"));
        }


        User user = userService.findById(id);
        user.setImageLinks(avatarPath.startsWith("/") ? avatarPath.substring(1) : avatarPath);

        userService.save(user);


        session.setAttribute("user", user);

        return ResponseEntity.ok(Map.of("success", true, "avatar", "/" + user.getImageLinks()));
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
            ra.addAttribute("cartError", "Bạn đã thêm game này vào giỏ hàng rồi");
        } else {
            userGameService.saveUserGame(new UserGame(user, game, java.time.LocalDateTime.now(), 0));
            ra.addAttribute("cartSuccess", "Đã thêm vào giỏ hàng!");
        }


        return "redirect:/welcome/gamedetail/{game_id}";
    }

    @GetMapping("/editprofile/{id}")
    public String editProfile(Model model, @PathVariable(value = "id") UUID id, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/welcome/about";
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
    public String gamePay(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) return "redirect:/welcome/about";
        User user = (User) session.getAttribute("user");

        List<UserGame> userGames = userGameService.getGamesByUser(user);
        userGames.sort(Comparator.comparing(UserGame::getPurchaseDate));

        Map<LocalDate, Double> spendByDate = new TreeMap<>();
        for (UserGame ug : userGames) {
            if (ug.getPurchaseDate() == null || ug.getGame() == null) continue;
            if (ug.getStatus() != 1) continue;
            spendByDate.merge(ug.getPurchaseDate().toLocalDate(), ug.getGame().getPrice(), Double::sum);
        }

        LocalDate start = user.getDateCreateAccount() != null
                ? user.getDateCreateAccount().toLocalDate()
                : (spendByDate.isEmpty() ? LocalDate.now() : spendByDate.keySet().iterator().next());

        List<String> labels = new ArrayList<>();
        List<Double> cumulative = new ArrayList<>();
        double totalDouble = 0.0;

        labels.add(start.toString());
        cumulative.add(0.0);

        for (Map.Entry<LocalDate, Double> e : spendByDate.entrySet()) {
            if (e.getKey().isBefore(start)) continue;
            totalDouble += e.getValue();
            labels.add(e.getKey().toString());
            cumulative.add(totalDouble);
        }


        long balance = Math.round(totalDouble);

        model.addAttribute("timecreateAcc", user.getDateCreateAccount());
        model.addAttribute("labels", labels);
        model.addAttribute("spendingData", cumulative);
        model.addAttribute("balance", balance);
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
        if (session.getAttribute("user") == null) return "redirect:/welcome/about";
        User user = userService.findById(UUID.fromString("6CE0FCF6-B584-4A63-AEDF-FAED48E78665"));
        LocalDateTime timeEnd = LocalDateTime.now().plusMinutes(1);

        int day = timeEnd.getDayOfMonth();
        int hour = timeEnd.getHour();
        int minute = timeEnd.getMinute();
        int second = timeEnd.getSecond();

        String statuss = "changePass||" + day + "||" + hour + "||" + minute + "||" + second;

        user.setStatus(statuss);
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
