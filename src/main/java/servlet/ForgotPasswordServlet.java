package servlet;

import service.AuthService;
import service.PasswordResetTicket;
import util.EmailUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ForgotPasswordServlet — gửi link đặt lại mật khẩu qua email.
 *
 * <p>Phản hồi giống hệt nhau dù email có tồn tại hay không, để trang này không trở thành
 * công cụ dò xem email nào đã đăng ký.
 */
@WebServlet("/forgotPassword")
public class ForgotPasswordServlet extends HttpServlet {

    private static final String VIEW = "/pages/forgotPassword.jsp";

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập email.");
            req.getRequestDispatcher(VIEW).forward(req, resp);
            return;
        }

        PasswordResetTicket ticket = authService.issueResetToken(email);
        if (ticket != null) {
            EmailUtil.sendPasswordResetEmail(
                    ticket.email(),
                    ticket.fullName(),
                    buildResetLink(req, ticket.token()));
        }

        req.setAttribute("success",
                "Nếu email này đã đăng ký, chúng tôi vừa gửi link đặt lại mật khẩu tới hộp thư"
                + " của bạn (vui lòng kiểm tra cả thư mục spam). Link có hiệu lực "
                + authService.getResetTokenValidMinutes() + " phút.");
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    private String buildResetLink(HttpServletRequest req, String token) {
        StringBuilder baseUrl = new StringBuilder()
                .append(req.getScheme()).append("://").append(req.getServerName());
        if (isNonDefaultPort(req)) {
            baseUrl.append(':').append(req.getServerPort());
        }
        return baseUrl.append(req.getContextPath())
                .append("/resetPassword?token=")
                .append(token)
                .toString();
    }

    private boolean isNonDefaultPort(HttpServletRequest req) {
        return ("http".equals(req.getScheme()) && req.getServerPort() != 80)
                || ("https".equals(req.getScheme()) && req.getServerPort() != 443);
    }
}
