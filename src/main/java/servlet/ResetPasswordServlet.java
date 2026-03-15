package servlet;

import dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String token = req.getParameter("token");
        if (token == null || token.isEmpty()) {
            req.setAttribute("error", "Link không hợp lệ.");
            req.getRequestDispatcher("/pages/resetPassword.jsp").forward(req, resp);
            return;
        }
        String email = userDAO.getEmailByToken(token);
        if (email == null) {
            req.setAttribute("error", "Link đã hết hạn hoặc không hợp lệ. Vui lòng yêu cầu gửi lại link.");
            req.getRequestDispatcher("/pages/resetPassword.jsp").forward(req, resp);
            return;
        }
        req.setAttribute("token", token);
        req.getRequestDispatcher("/pages/resetPassword.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String token = req.getParameter("token");
        String newPass = req.getParameter("newPassword");
        String newPass2 = req.getParameter("newPassword2");
        if (token == null || token.isEmpty()) {
            req.setAttribute("error", "Link không hợp lệ.");
            req.getRequestDispatcher("/pages/resetPassword.jsp").forward(req, resp);
            return;
        }
        String email = userDAO.getEmailByToken(token);
        if (email == null) {
            req.setAttribute("error", "Link đã hết hạn. Vui lòng yêu cầu gửi lại link từ trang Quên mật khẩu.");
            req.getRequestDispatcher("/pages/resetPassword.jsp").forward(req, resp);
            return;
        }
        if (newPass == null || newPass.length() < 6) {
            req.setAttribute("error", "Mật khẩu mới tối thiểu 6 ký tự.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/pages/resetPassword.jsp").forward(req, resp);
            return;
        }
        if (!newPass.equals(newPass2)) {
            req.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/pages/resetPassword.jsp").forward(req, resp);
            return;
        }
        if (userDAO.updatePasswordByEmail(email, newPass)) {
            userDAO.deleteResetToken(token);
            resp.sendRedirect(req.getContextPath() + "/login?msg=reset");
            return;
        }
        req.setAttribute("error", "Đặt lại mật khẩu thất bại. Vui lòng thử lại.");
        req.setAttribute("token", token);
        req.getRequestDispatcher("/pages/resetPassword.jsp").forward(req, resp);
    }
}
