package util;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;

public class EmailUtil {

    private static final String FROM_EMAIL   = "nl034993@gmail.com"; // ← Gmail của bạn
    private static final String APP_PASSWORD = "afofnecrzxmfhsft";  // ← App Password 16 ký tự
    private static final String FROM_NAME    = "DWatch Shop";

    public static void sendOrderConfirmation(String toEmail, String customerName,
                                             int orderID, double total,
                                             String address, String itemsHTML) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(587);
            email.setAuthenticator(new DefaultAuthenticator(FROM_EMAIL, APP_PASSWORD));
            email.setStartTLSEnabled(true);
            email.setFrom(FROM_EMAIL, FROM_NAME);
            email.setSubject("DWatch - Xác nhận đơn hàng #" + orderID);
            email.addTo(toEmail, customerName);
            email.setCharset("UTF-8");
            email.setHtmlMsg(buildEmailBody(customerName, orderID, total, address, itemsHTML));
            email.send();
            System.out.println("✔ Email đã gửi tới: " + toEmail);
        } catch (Exception e) {
            System.err.println("✘ Lỗi gửi email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String buildEmailBody(String name, int orderID,
                                         double total, String address, String itemsHTML) {
        return String.format("""
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;
                        border:1px solid #ddd;border-radius:8px;overflow:hidden">
              <div style="background:#1a1a1a;padding:24px;text-align:center">
                <h1 style="color:#c9a84c;margin:0">&#8987; DWatch</h1>
                <p style="color:#fff;margin:4px 0">Cảm ơn bạn đã đặt hàng!</p>
              </div>
              <div style="padding:24px">
                <p>Xin chào <strong>%s</strong>,</p>
                <p>Đơn hàng <strong>#%d</strong> của bạn đã được xác nhận thành công.</p>
                <table style="width:100%%;border-collapse:collapse;margin:16px 0">
                  <thead>
                    <tr style="background:#f5f5f5">
                      <th style="padding:10px;text-align:left;border-bottom:2px solid #ddd">Sản phẩm</th>
                      <th style="padding:10px;text-align:right;border-bottom:2px solid #ddd">Thành tiền</th>
                    </tr>
                  </thead>
                  <tbody>%s</tbody>
                </table>
                <div style="text-align:right;font-size:18px;font-weight:bold;color:#c9a84c">
                  Tổng cộng: %,.0f&#8363;
                </div>
                <hr style="margin:16px 0">
                <p><strong>&#128205; Địa chỉ giao hàng:</strong><br>%s</p>
                <p style="color:#888;font-size:13px">
                  Chúng tôi sẽ liên hệ xác nhận và giao hàng trong 2-3 ngày làm việc.
                </p>
              </div>
              <div style="background:#1a1a1a;padding:16px;text-align:center">
                <p style="color:#888;margin:0;font-size:13px">© 2025 DWatch — Đồng Hồ Chính Hãng</p>
              </div>
            </div>
            """, name, orderID, itemsHTML, total, address);
    }
}