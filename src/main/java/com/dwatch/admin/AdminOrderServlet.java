package com.dwatch.admin;

import com.dwatch.common.EmailUtil;
import com.dwatch.common.Pagination;
import com.dwatch.order.Order;
import com.dwatch.order.OrderDAO;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Admin order management: list/filter orders and update their status.
 */
@WebServlet("/admin/orders")
public class AdminOrderServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }

        String statusFilter = req.getParameter("status");
        int page = Pagination.parsePage(req.getParameter("page"));
        int total = orderDAO.countAllOrders(statusFilter);
        Pagination pagination = new Pagination(page, PAGE_SIZE, total);
        List<Order> orders = orderDAO.getAllOrders(pagination.getCurrentPage(), PAGE_SIZE, statusFilter);

        req.setAttribute("orders", orders);
        req.setAttribute("currentPage", pagination.getCurrentPage());
        req.setAttribute("totalPages", pagination.getTotalPages());
        req.setAttribute("statusFilter", statusFilter);
        req.setAttribute("statuses", Order.orderedStatuses());
        req.getRequestDispatcher("/pages/adminOrders.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }
        req.setCharacterEncoding("UTF-8");

        int orderID = Integer.parseInt(req.getParameter("orderID"));
        String newStatus = req.getParameter("newStatus");
        String previousStatus = orderDAO.getStatus(orderID);
        boolean updated = isValidStatus(newStatus) && orderDAO.updateStatus(orderID, newStatus);

        if (updated && !newStatus.equals(previousStatus)) {
            notifyStatusChange(orderID, newStatus);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/orders?msg=" + (updated ? "updated" : "error"));
    }

    /** Gửi email cho khách khi trạng thái đơn thay đổi. Lỗi gửi email không được làm hỏng việc cập nhật trạng thái. */
    private void notifyStatusChange(int orderID, String newStatus) {
        Order order = orderDAO.getOrderByID(orderID);
        if (order == null || order.getEmail() == null || order.getEmail().isBlank()) return;
        EmailUtil.sendOrderStatusUpdate(order.getEmail(), order.getFullName(), orderID, Order.labelFor(newStatus));
    }

    private boolean isValidStatus(String status) {
        if (status == null) return false;
        for (String s : Order.orderedStatuses()) {
            if (s.equals(status)) return true;
        }
        return false;
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"));
    }
}
