    package WebBackEnd.Controller;

    import WebBackEnd.SucDat.GameCore;
    import WebBackEnd.SucDat.SendMailTest;
    import WebBackEnd.Entity.Feedback;
    import WebBackEnd.Entity.Game;
    import WebBackEnd.Entity.User;
    import WebBackEnd.Entity.UserGame;
    import WebBackEnd.repository.UserRepository;
    import WebBackEnd.service.*;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpSession;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
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
        private SendMailTest  sendMailTest;
        @Autowired
        private FeedbackService feedbackService;
        @Autowired
        private GameCore gameCore;


        @GetMapping
        public String homepage(Model model) {
            if (!model.containsAttribute("showForm")) {
                model.addAttribute("showForm", "");
            }
            model.addAttribute("gameMain", gameSevice.findGameByStatus("main"));

            model.addAttribute("listGame", gameSevice.list20GameIntoGame());
            model.addAttribute("linkimage", GameCore.imageLinkGame(gameSevice.findGameByStatus("main").getImageLinks()));


            //        for(Game a : gameSevice.list20GameIntoGame()){
    //            System.out.println(a.getDeceptions()[4]);
    //        }
    //        UUID uid = UUID.fromString("6CE0FCF6-B584-4A63-AEDF-FAED48E78665");
    //        for (Game a : userGameService.showGameInProfile(uid)) {
    //            System.out.println(a.getGameName());
    //        }


            return "HTML/Index";
        }

        @GetMapping("/test")
        public String homepage2(Model model) {
            return "HTML/seat.html";
        }



            @GetMapping("/Cart/{id}")
            public String payMent(@PathVariable UUID id, Model model) {
                model.addAttribute("listGame", userGameService.showGameInCart(id));
                model.addAttribute("user", userService.findById(id));
                UUID gameid = UUID.fromString("D7E2F9B4-5A1C-4B8D-8F2E-3C7A9D6E2F41");

                return "HTML/Cart";
            }

        @PostMapping("/buy")
        public String buy(@RequestParam("selectedIds") List<UUID> selectedIds,
                          Model model,
                          HttpSession session) {
            UUID userId = (UUID) session.getAttribute("id");
            if (userId == null) return "redirect:/welcome/login";

            if (selectedIds == null || selectedIds.isEmpty()) {

                return "redirect:/welcome/Cart/" + userId;
            }

            List<Game> list = new ArrayList<>();

            for(UUID id : selectedIds){
                list.add(gameSevice.findGameById(id));
            }

            model.addAttribute("listGame", list);
            model.addAttribute("user", userService.findById(userId));
            session.setAttribute("checkoutSelectedIds", selectedIds);
            return "HTML/Buy";
        }

        @PostMapping("/checkout")
        public String checkout(@RequestParam("selectedIds") List<UUID> selectedIds,
                               @RequestParam("userId") UUID userIdFromForm,
                               HttpSession session,
                               Model model,
                               RedirectAttributes ra) {


            UUID sessionUserId = (UUID) session.getAttribute("id");
            if (sessionUserId == null) {
                return "redirect:/welcome/login";
            }
            if (!sessionUserId.equals(userIdFromForm)) {

                ra.addFlashAttribute("message", "Phiên không hợp lệ. Vui lòng thử lại.");
                return "redirect:/welcome/Cart/" + sessionUserId;
            }

        if (selectedIds == null || selectedIds.isEmpty()) {
                @SuppressWarnings("unchecked")
                List<UUID> idsInSession = (List<UUID>) session.getAttribute("checkoutSelectedIds");
                if (idsInSession == null || idsInSession.isEmpty()) {
                    return "redirect:/welcome/Cart/" + sessionUserId;
                }
                selectedIds = idsInSession;
            }


            User user = userService.findById(sessionUserId);

            List<Game> list = new ArrayList<>();
            for (UUID id : selectedIds) {
                Game g = gameSevice.findGameById(id);
                if (g != null) list.add(g);
            }

            if (list.isEmpty()) {
                ra.addFlashAttribute("message", "Không tìm thấy game hợp lệ để thanh toán.");
                return "redirect:/welcome/Cart/" + sessionUserId;
            }

            double total = 0d;
            for (Game g : list) {
                total += (g.getPrice() == 0 ? 0 : g.getPrice());
            }


            if (user.getPrice() >= total) {
                user.setPrice(user.getPrice() - total);
                userService.save(user);

                for (Game g : list) {
                    UserGame ug = new UserGame(user, g, LocalDateTime.now(), 1);
                    userGameService.saveUserGame(ug);
                }


                session.removeAttribute("checkoutSelectedIds");

                ra.addFlashAttribute("message", "Thanh toán thành công!");
                return "HTML/Succest";
            } else {
                model.addAttribute("thongbao", "khongdusodu");
                model.addAttribute("listGame", list);
                model.addAttribute("user", user);

                return "HTML/Buy";
            }
        }







        @PostMapping("/register")
        @ResponseBody
        public Map<String, Object> registerAjax(@RequestBody User user) {
            Map<String, Object> response = new HashMap<>();
            String username = user.getUsername();
            String email = user.getEmail();


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



            user.setUsername(username);
            user.setEmail(email);
            user.setScore(0);
            user.setStatus("wait");
            user.setDateCreateAccount(LocalDateTime.now());
            userRepository.save(user);


            String input = "wait"+ user.getId();
            String fi;
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(input.getBytes());

                StringBuilder sb = new StringBuilder();
                for (byte b : digest) {
                    sb.append(String.format("%02x", b));
                }
                fi= sb.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            String title = "Xác nhận tài khoản của bạn";

            String link = "https://a7c804c1ed63.ngrok-free.app/veryAccount/done/"
                    + user.getId() + "/" + fi;

            String content =
                    "<p>Hãy nhấp vào liên kết dưới đây để kích hoạt tài khoản của bạn:</p>"
                            + "<p><a href=\"" + link + "\">Nhấn vào đây để kích hoạt</a></p>"
                            + "<p>Nếu không bấm được, copy link sau dán vào trình duyệt:<br>"
                            + link + "</p>";


            sendMailTest.testSend(user.getEmail(), title, content);



            response.put("status", "success");
            response.put("message", "Đăng ký thành công! Một đường link xác thực tài khoảng đã được gửi vào email của bạn .");
            return response;
        }









        @PostMapping("/login")
        @ResponseBody
        public ResponseEntity<?> login(@RequestParam String username,
                                       @RequestParam String password,
                                       HttpSession session) {

            var user = userService.findByUsername(username);

            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tài khoản không tồn tại!"));
            }

            if (!password.equals(user.getPassword())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Sai mật khẩu!"));
            }
            if(user.getStatus().equals("wait")){
                return ResponseEntity.badRequest().body(Map.of("error","Tài khoảng chưa được kích hoạt"));
            }

            session.setAttribute("id", user.getId());
            session.setAttribute("username", user.getUsername());

            return ResponseEntity.ok(Map.of("success", true));
        }



        @GetMapping("/about")
        public String controllAbout(Model model) {
            return "HTML/About";
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
            model.addAttribute("tieude",product);
            return "HTML/Category";
        }



        @GetMapping("/gamedetail/{game_id}")
        public String gameDetail(@PathVariable("game_id") UUID game_id,
                                 Model model,
                                 HttpSession session) {

            Game game = gameSevice.findGameById(game_id);
            model.addAttribute("game", game);


            UUID user_id = (UUID) session.getAttribute("id");


            boolean userGame = userGameService.findUserGameByUserAndGame(
                    userService.findById(user_id),
                    game
            );
            Feedback feedback = null;
            List<Feedback> list= feedbackService.findFeedbackByGameId(game_id);
            if(user_id!=null){
                for(Feedback a: list){
                    if(user_id.toString().equalsIgnoreCase(a.getUserId().toString())){
                        feedback = a;
                        list.remove(a);
                        break;
                    }
                }
            }

            model.addAttribute("UserGame", userGame);
            model.addAttribute("listFeedback" , list);
            model.addAttribute("myFeedback" , feedback);
            return "HTML/GameDetail";
        }


        @PostMapping("/gamedetail/{game_id}")
        public String saveFeedback(@PathVariable("game_id") UUID gameId,
                                   @RequestParam("star") Double star,
                                   @RequestParam("comment") String cmt,
                                   HttpSession session) {

            UUID userId = (UUID) session.getAttribute("id");
            if(userId == null) return "redirect:/welcome/login";

            List<Feedback> lst = feedbackService.findFeedbackByGameId(gameId);
            Feedback my = lst.stream()
                    .filter(f -> f.getUserId().equals(userId))
                    .findFirst().orElse(null);

            if(my == null){
                my = new Feedback(gameId, userId, cmt, star);
            } else {
                my.setStar(star);
                my.setComment(cmt);
            }
            feedbackService.saveFeedback(my);

            return "redirect:/welcome/gamedetail/" + gameId;
        }





        @GetMapping("/Newgame")
        public String newgame(Model model) {
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
        public String userDetail(@PathVariable(value = "id") UUID id,
                                 Model model) {
            model.addAttribute("listGame", userGameService.showGameInProfile(id));
            model.addAttribute("user", userService.findById(id));

            return "HTML/ProfileUser";
        }

        @PostMapping("/addGameToCard/{game_id}")
        public String addGameToCard(@PathVariable("game_id") UUID gameId,
                                    HttpSession session,
                                    RedirectAttributes ra) {
            UUID userId = (UUID) session.getAttribute("id");
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



        @Controller
        public class AuthController {
            @GetMapping("/logout")
            public String logout(HttpServletRequest request) {
                request.getSession().invalidate();
                return "redirect:/welcome";
            }
        }



        //    @PostMapping("/home")
    //    public String doLogin(@RequestParam("username") String username,
    //                          @RequestParam("password") String password,
    //                          Model model) {
    //        if ("admin".equals(username) && "123".equals(password)) {
    //            model.addAttribute("title", "Xin chào " + username) ;
    //            return "home/welcome";
    //        } else {
    //            model.addAttribute("title", "Sai username hoặc password");
    //            return "home/welcome";
    //        }
    //    }

    }
