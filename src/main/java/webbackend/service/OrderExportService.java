package webbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webbackend.entity.*;
import webbackend.repository.UserGameArchiveRepository;
import webbackend.repository.UserGameRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class OrderExportService {

    @Autowired
    private UserGameRepository userGameRepository;

    @Autowired
    private UserGameArchiveRepository userGameArchiveRepository;

    public List<OrderExportDTO> getAllOrdersForExport() {
        List<OrderExportDTO> result = new ArrayList<>();


        List<UserGame> userGames = userGameRepository.findAll();
        for (UserGame ug : userGames) {
            OrderExportDTO dto = new OrderExportDTO();

            dto.setOrderId(ug.getId());
            dto.setCustomerName(ug.getUser() != null ? ug.getUser().getUsername() : "");
            dto.setGameName(ug.getGame() != null ? ug.getGame().getGameName() : "");

            Double basePrice = ug.getGame() != null ? ug.getGame().getPrice() : ug.getPurchasePrice();
            dto.setBasePrice(basePrice);


            if (ug.getVouncher() != null) {
                Vouncher v = ug.getVouncher();

                if ("PERCENT".equalsIgnoreCase(v.getType())) {
                    dto.setDiscountText(v.getSale() + " %");
                } else {
                    dto.setDiscountText(String.format("%,.0f ₫", v.getSale()));
                }
            } else {
                dto.setDiscountText("");
            }

            dto.setTotalAmount(ug.getPurchasePrice());
            dto.setPaymentMethod("WALLET");
            dto.setStatusLabel("Đã mua");

            LocalDateTime orderDate = ug.getPurchaseDate();
            dto.setOrderDate(orderDate);

            dto.setStaffName(ug.getStaff() != null ? ug.getStaff().getAdminName() : "");

            result.add(dto);
        }


        List<UserGameArchive> archives = userGameArchiveRepository.findAll();
        for (UserGameArchive uga : archives) {
            OrderExportDTO dto = new OrderExportDTO();

            dto.setOrderId(uga.getId());
            dto.setCustomerName(uga.getUser() != null ? uga.getUser().getUsername() : "");
            dto.setGameName(uga.getGame() != null ? uga.getGame().getGameName() : "");

            dto.setBasePrice(uga.getOriginalPrice());

            if (uga.getVouncher() != null) {
                Vouncher v = uga.getVouncher();
                if ("PERCENT".equalsIgnoreCase(v.getType())) {
                    dto.setDiscountText(v.getSale() + " %");
                } else {
                    dto.setDiscountText(String.format("%,.0f ₫", v.getSale()));
                }
            } else {
                dto.setDiscountText("");
            }


            dto.setTotalAmount(uga.getOriginalPrice());

            dto.setPaymentMethod("WALLET");

            String st = uga.getStatus() != null ? uga.getStatus().toLowerCase() : "";
            if (st.contains("refund")) {
                dto.setStatusLabel("Hoàn tiền");
            } else if (st.contains("expire")) {
                dto.setStatusLabel("Hết hạn");
            } else {
                dto.setStatusLabel(uga.getStatus());
            }

            LocalDateTime orderDate = uga.getPurchaseDate() != null
                    ? uga.getPurchaseDate().atStartOfDay()
                    : null;
            dto.setOrderDate(orderDate);

            dto.setStaffName(uga.getStaff() != null ? uga.getStaff().getAdminName() : "");

            result.add(dto);
        }

         result.sort(Comparator.comparing(OrderExportDTO::getOrderDate,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed());

        return result;
    }
}
