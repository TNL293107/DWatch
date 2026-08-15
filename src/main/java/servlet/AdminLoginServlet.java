package servlet;

import service.AdminAuthService;
import util.CsrfUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * AdminLoginServlet — đăng nhập quản trị.
 *
 * <p>Mật khẩu được xác thực bằng BCrypt trong {@link AdminAuthService}. Bản ghi seed
 * plaintext trong database.sql sẽ tự động được băm lại sau lần đăng nhập đúng đầu tiên
 * (xem database_password_hash.sql).
 */
@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {

    private final AdminAuthService adminAuthService = new AdminAuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/pages/adminLogin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (!adminAuthService.authenticate(username, password)) {
            req.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu.");
            req.getRequestDispatcher("/pages/adminLogin.jsp").forward(req, resp);
            return;
        }

        // Cấp session mới sau khi xác thực để chống session fixation.
        req.getSession(true);
        req.changeSessionId();
        HttpSession session = req.getSession();
        session.setAttribute("adminLoggedIn", Boolean.TRUE);
        session.setAttribute("adminUser", username);
        CsrfUtil.rotateToken(session);

        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }
}
