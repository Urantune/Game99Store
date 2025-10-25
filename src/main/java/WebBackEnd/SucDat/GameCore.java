package WebBackEnd.SucDat;

import WebBackEnd.model.Entity.Game;
import WebBackEnd.model.Entity.User;
import WebBackEnd.model.Entity.UserGame;
import WebBackEnd.model.Entity.Vouncher;
import WebBackEnd.repository.UserGameRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


public class GameCore {

    @Autowired
    private UserGameRepository userGameRepository;



    public static String[] imageLinkGame(String linkTotal){
        String link[] = linkTotal.split("\\|\\|");
        return link;
    }

    public static String[] deceptionGame(String deception){
        String deceptions[] = deception.split("\\|\\|");
        return deceptions;
    }

    /**
     * Thực hiện thanh toán 1 game cho người dùng.
     * @param game đối tượng game được mua
     * @param voucher voucher được áp dụng (nếu có)
     * @param user người dùng mua
     * @return true nếu thanh toán thành công, false nếu thất bại
     */
    public boolean payMent(Game game, Vouncher voucher, User user) {
        if (game == null || user == null) {
            return false;
        }

        double price = game.getPrice();

        // Nếu có voucher, áp dụng giảm giá
        if (voucher != null) {
            LocalDateTime now = LocalDateTime.now();
            // Kiểm tra ngày hợp lệ
            if (voucher.getDate_start().isBefore(now) && voucher.getDate_end().isAfter(now)) {
                price = price - (price * voucher.getSale() / 100);
            }
        }

        // Tạo đối tượng UserGame
        UserGame userGame = new UserGame(user, game, LocalDateTime.now(), price);

        // Lưu xuống database
        try {
            userGameRepository.save(userGame);
            return true;
        } catch (Exception e) {
            System.out.println("Lỗi thanh toán: " + e.getMessage());
            return false;
        }
    }


}
