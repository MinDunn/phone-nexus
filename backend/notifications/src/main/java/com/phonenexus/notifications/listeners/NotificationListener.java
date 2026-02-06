package com.phonenexus.notifications.listeners;

import com.phonenexus.notifications.config.RabbitMQConfig;
import com.phonenexus.notifications.events.OrderEvent;
import com.phonenexus.notifications.events.StockEvent;
import com.phonenexus.notifications.events.UserEvent;
import com.phonenexus.notifications.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationListener {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NotificationListener.class);

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderEvent(OrderEvent event) {
        log.info("Processing order event for ID: {} with status: {}", event.orderId(), event.status());

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("orderId", event.orderId());
            variables.put("customerName", event.customerName());
            variables.put("totalAmount", event.totalAmount());
            variables.put("status", event.status());
            variables.put("items", event.items());

            String subject;
            String templateName;

            switch (event.status().toUpperCase()) {
                case "PAID":
                    subject = "Xác nhận đơn hàng #" + event.orderId() + " - PhoneNexus";
                    templateName = "order-success";
                    break;
                case "SHIPPED":
                    subject = "Đơn hàng #" + event.orderId() + " đang được giao - PhoneNexus";
                    templateName = "order-status-update";
                    break;
                case "COMPLETED":
                    subject = "Đơn hàng #" + event.orderId() + " đã hoàn tất - PhoneNexus";
                    templateName = "order-status-update";
                    break;
                case "RETURNED":
                    subject = "Xác nhận hoàn trả đơn hàng #" + event.orderId() + " - PhoneNexus";
                    templateName = "order-status-update";
                    break;
                default:
                    subject = "Cập nhật trạng thái đơn hàng #" + event.orderId() + " - PhoneNexus";
                    templateName = "order-status-update";
            }

            emailService.sendEmail(event.email(), subject, templateName, variables);
            log.info("Successfully sent {} email for ID: {}", event.status(), event.orderId());
        } catch (Exception e) {
            log.error("Failed to process order event for ID: {}", event.orderId(), e);
            throw e; // Re-throw to trigger RabbitMQ retry/DLQ
        }
    }

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void handleUserEvent(UserEvent event) {
        log.info("Processing user verification event for UserID: {}", event.userId());

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("fullName", event.fullName());
            variables.put("verificationUrl", event.verificationUrl());

            emailService.sendEmail(
                    event.email(),
                    "Kích hoạt tài khoản PhoneNexus",
                    "verification",
                    variables);
            log.info("Successfully sent verification email for UserID: {}", event.userId());
        } catch (Exception e) {
            log.error("Failed to process user verification for UserID: {}", event.userId(), e);
            throw e; // Re-throw to trigger RabbitMQ retry/DLQ
        }
    }

    @RabbitListener(queues = RabbitMQConfig.ADMIN_QUEUE)
    public void handleStockEvent(StockEvent event) {
        log.info("Processing low stock alert for SKU: {}", event.sku());

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("productName", event.productName());
            variables.put("sku", event.sku());
            variables.put("currentStock", event.currentStock());

            // In a real scenario, this would go to an admin email list
            emailService.sendEmail(
                    "admin@phonenexus.com",
                    "CẢNH BÁO: Tồn kho thấp - SKU: " + event.sku(),
                    "stock-alert",
                    variables);
            log.info("Successfully sent low stock alert for SKU: {}", event.sku());
        } catch (Exception e) {
            log.error("Failed to process stock alert for SKU: {}", event.sku(), e);
            throw e; // Re-throw to trigger RabbitMQ retry/DLQ
        }
    }
}
