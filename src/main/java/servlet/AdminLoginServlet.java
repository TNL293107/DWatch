package servlet;

import util.DBUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

/**
 * AdminLoginServlet — simple admin authentication.
 * Default credentials set in database.sql: admin / admin123
 */
@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {

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

        if (authenticate(username, password)) {
            HttpSession session = req.getSession(true);
            session.setAttribute("adminLoggedIn", Boolean.TRUE);
            session.setAttribute("adminUser", username);
            resp.sendRedirect(req.getContextPath() + "/admin/products");
        } else {
            req.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu.");
            req.getRequestDispatcher("/pages/adminLogin.jsp").forward(req, resp);
        }
    }

    private boolean authenticate(String username, String password) {
        String sql = "SELECT 1 FROM Admin WHERE Username = ? AND Password = ?";
        try (Connection cn = DBUtil.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
