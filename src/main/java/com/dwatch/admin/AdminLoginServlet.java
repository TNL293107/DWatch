package com.dwatch.admin;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * AdminLoginServlet — simple admin authentication.
 * Default credentials set in database.sql: admin / admin123
 */
@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {

    private final AdminDAO adminDAO = new AdminDAO();

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

        if (adminDAO.authenticate(username, password)) {
            HttpSession session = req.getSession(true);
            session.setAttribute("adminLoggedIn", Boolean.TRUE);
            session.setAttribute("adminUser", username);
            resp.sendRedirect(req.getContextPath() + "/admin/products");
        } else {
            req.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu.");
            req.getRequestDispatcher("/pages/adminLogin.jsp").forward(req, resp);
        }
    }
}
