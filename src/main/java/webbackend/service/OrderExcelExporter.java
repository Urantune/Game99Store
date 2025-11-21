package webbackend.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import webbackend.entity.OrderExportDTO;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderExcelExporter {

    private final List<OrderExportDTO> orders;

    public OrderExcelExporter(List<OrderExportDTO> orders) {
        this.orders = orders;
    }

    public void export(HttpServletResponse response) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");


        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);

        String[] headers = {
                "Mã đơn",
                "Tên khách mua game",
                "Tên game",
                "Giá vốn",
                "Giảm giá",
                "Tổng tiền thu",
                "Hình thức thanh toán",
                "Trạng thái",
                "Ngày đặt hàng",
                "Nhân viên"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        int rowIdx = 1;
        for (OrderExportDTO o : orders) {
            Row row = sheet.createRow(rowIdx++);

            int col = 0;

            row.createCell(col++).setCellValue(
                    o.getOrderId() != null ? o.getOrderId().toString() : ""
            );
            row.createCell(col++).setCellValue(
                    o.getCustomerName() != null ? o.getCustomerName() : ""
            );
            row.createCell(col++).setCellValue(
                    o.getGameName() != null ? o.getGameName() : ""
            );
            row.createCell(col++).setCellValue(
                    o.getBasePrice() != null ? o.getBasePrice() : 0
            );
            row.createCell(col++).setCellValue(
                    o.getDiscountText() != null ? o.getDiscountText() : ""
            );
            row.createCell(col++).setCellValue(
                    o.getTotalAmount() != null ? o.getTotalAmount() : 0
            );
            row.createCell(col++).setCellValue(
                    o.getPaymentMethod() != null ? o.getPaymentMethod() : ""
            );
            row.createCell(col++).setCellValue(
                    o.getStatusLabel() != null ? o.getStatusLabel() : ""
            );
            row.createCell(col++).setCellValue(
                    o.getOrderDate() != null ? dtf.format(o.getOrderDate()) : ""
            );
            row.createCell(col++).setCellValue(
                    o.getStaffName() != null ? o.getStaffName() : ""
            );
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
