package com.dwatch.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Read-only admin dashboard: revenue, order counts by status, top products.
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private static final int TOP_PRODUCTS_LIMIT = 5;
    private static final int REVENUE_DAYS = 14;

    private final StatsDAO statsDAO = new StatsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }

        req.setAttribute("totalRevenue", statsDAO.getTotalRevenue());
        req.setAttribute("orderCountByStatus", statsDAO.getOrderCountByStatus());
        req.setAttribute("topProducts", statsDAO.getTopProducts(TOP_PRODUCTS_LIMIT));
        req.setAttribute("revenueByDay", statsDAO.getRevenueByDay(REVENUE_DAYS));
        req.getRequestDispatcher("/pages/adminDashboard.jsp").forward(req, resp);
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"));
    }
}
