package util;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;

public class EmailUtil {

    private static final String FROM_EMAIL   = "nl034993@gmail.com"; // ← Gmail của bạn
    private static final String APP_PASSWORD = "afofnecrzxmfhsft";  // ← App Password 16 ký tự
    private static final String FROM_NAME    = "DWatch Shop";

    public static void sendOrderConfirmation(String toEmail, String customerName,
                                             int orderID, double total,
                                             String address, String itemsHTML,
                                             String paymentStatus) {
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
            email.setHtmlMsg(buildEmailBody(customerName, orderID, total, address, itemsHTML, paymentStatus));
            email.send();
            System.out.println("✔ Email đã gửi tới: " + toEmail);
        } catch (Exception e) {
            System.err.println("✘ Lỗi gửi email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String buildEmailBody(String name, int orderID,
                                         double total, String address, String itemsHTML, String paymentStatus) {
        String statusText = (paymentStatus != null && !paymentStatus.isEmpty()) ? paymentStatus : "Chưa thanh toán";
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
                <p><strong>Tình trạng đơn hàng:</strong> <span style="color:#c9a84c;font-weight:bold">%s</span></p>
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
            """, name, orderID, statusText, itemsHTML, total, address);
    }

    /** Gửi email chứa link đặt lại mật khẩu (quên mật khẩu) */
    public static void sendPasswordResetEmail(String toEmail, String customerName, String resetLink) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(587);
            email.setAuthenticator(new DefaultAuthenticator(FROM_EMAIL, APP_PASSWORD));
            email.setStartTLSEnabled(true);
            email.setFrom(FROM_EMAIL, FROM_NAME);
            email.setSubject("DWatch - Đặt lại mật khẩu");
            email.addTo(toEmail, customerName);
            email.setCharset("UTF-8");
            String body = String.format("""
                <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto;padding:24px">
                  <h2 style="color:#1a1a1a">Đặt lại mật khẩu</h2>
                  <p>Xin chào <strong>%s</strong>,</p>
                  <p>Bạn đã yêu cầu đặt lại mật khẩu. Nhấn vào link bên dưới (link có hiệu lực 1 giờ):</p>
                  <p style="margin:24px 0"><a href="%s" style="background:#c9a84c;color:#fff;padding:12px 24px;text-decoration:none;border-radius:4px;display:inline-block">Đặt lại mật khẩu</a></p>
                  <p style="color:#666;font-size:13px">Nếu bạn không yêu cầu, hãy bỏ qua email này.</p>
                  <hr style="margin:24px 0">
                  <p style="color:#888;font-size:12px">© 2025 DWatch</p>
                </div>
                """, customerName, resetLink);
            email.setHtmlMsg(body);
            email.send();
            System.out.println("✔ Email đặt lại mật khẩu đã gửi tới: " + toEmail);
        } catch (Exception e) {
            System.err.println("✘ Lỗi gửi email đặt lại mật khẩu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}