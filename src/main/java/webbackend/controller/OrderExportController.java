package webbackend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import webbackend.entity.OrderExportDTO;
import webbackend.service.OrderExcelExporter;
import webbackend.service.OrderExportService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/welcomeAdmin")
public class OrderExportController {

    @Autowired
    private OrderExportService orderExportService;

    @GetMapping("/export/orders")
    public void exportOrders(HttpServletResponse response) throws IOException {
        String fileName = "orders_export.xlsx";

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerValue = "attachment; filename=\"" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + "\"";
        response.setHeader("Content-Disposition", headerValue);

        List<OrderExportDTO> orders = orderExportService.getAllOrdersForExport();
        OrderExcelExporter exporter = new OrderExcelExporter(orders);
        exporter.export(response);
    }
}
