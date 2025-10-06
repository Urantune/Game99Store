package util;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.*;
import java.util.Properties;

public class EmailUtil {

    public static void sendEmail(String to, String subject, String messageHtml) throws Exception {
        String host = "smtp.gmail.com";
        String user = "tritrongdo111@gmail.com";
        String pass = "inbd cejq emnd bnyv";

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Tạo session có xác thực
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        // Tạo message
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(user));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);

        // gửi HTML thay vì plain text
        msg.setContent(messageHtml, "text/html; charset=UTF-8");

        // Gửi
        Transport.send(msg);
        System.out.println("✅ Email đã gửi thành công đến: " + to);
    }

    public static void sendOTP(String to, String otp) throws Exception {
        sendEmail(to, "Your OTP Code",
                "<p>Mã OTP của bạn là: <b>" + otp + "</b></p>"
                + "<p>Mã này sẽ hết hạn sau <b>5 phút</b>.</p>");
    }

    public static void sendEmailHtml(String to, String subject, String htmlContent) throws Exception {
        String host = "smtp.gmail.com";
        String user = "tritrongdo111@gmail.com";
        String pass = "inbd cejq emnd bnyv";

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(user));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        msg.setSubject(subject);

        // Gửi HTML thay vì plain text
        msg.setContent(htmlContent, "text/html; charset=UTF-8");

        Transport.send(msg);
        System.out.println("✅ Email HTML đã gửi thành công đến: " + to);
    }

}
