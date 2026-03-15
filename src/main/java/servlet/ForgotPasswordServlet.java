package servlet;

import dao.UserDAO;
import model.User;
import util.EmailUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@WebServlet("/forgotPassword")
public class ForgotPasswordServlet extends HttpServlet {

    private static final int TOKEN_VALID_MINUTES = 60;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/pages/forgotPassword.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String email = req.getParameter("email");
        if (email == null || (email = email.trim()).isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập email.");
            req.getRequestDispatcher("/pages/forgotPassword.jsp").forward(req, resp);
            return;
        }
        User user = userDAO.findByEmail(email);
        if (user == null) {
            req.setAttribute("error", "Email chưa được đăng ký trong hệ thống.");
            req.getRequestDispatcher("/pages/forgotPassword.jsp").forward(req, resp);
            return;
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        Date expiry = new Date(System.currentTimeMillis() + TOKEN_VALID_MINUTES * 60 * 1000L);
        userDAO.saveResetToken(email, token, expiry);
        String baseUrl = req.getScheme() + "://" + req.getServerName();
        if ((req.getScheme().equals("http") && req.getServerPort() != 80) ||
            (req.getScheme().equals("https") && req.getServerPort() != 443)) {
            baseUrl += ":" + req.getServerPort();
        }
        baseUrl += req.getContextPath();
        String resetLink = baseUrl + "/resetPassword?token=" + token;
        EmailUtil.sendPasswordResetEmail(email, user.getFullName(), resetLink);
        req.setAttribute("success", "Đã gửi link đặt lại mật khẩu tới email của bạn. Vui lòng kiểm tra hộp thư (và thư mục spam). Link có hiệu lực " + TOKEN_VALID_MINUTES + " phút.");
        req.getRequestDispatcher("/pages/forgotPassword.jsp").forward(req, resp);
    }
}
