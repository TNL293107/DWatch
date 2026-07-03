package com.dwatch.order;

import com.dwatch.common.Pagination;
import com.dwatch.user.User;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/orders")
public class OrderHistoryServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = getUser(req);
        String detailParam = req.getParameter("id");

        if (detailParam != null) {
            int orderID = Integer.parseInt(detailParam);
            Order order = orderDAO.getOrderByID(orderID);
            List<OrderDetail> details = orderDAO.getOrderDetails(orderID);
            req.setAttribute("order", order);
            req.setAttribute("details", details);
            req.getRequestDispatcher("/pages/orderDetail.jsp").forward(req, resp);
        } else {
            int page = Pagination.parsePage(req.getParameter("page"));
            int totalOrders = orderDAO.countOrdersByEmail(user.getEmail());
            Pagination pagination = new Pagination(page, PAGE_SIZE, totalOrders);
            List<Order> orders = orderDAO.getOrdersByEmail(user.getEmail(), pagination.getCurrentPage(), PAGE_SIZE);
            req.setAttribute("orders", orders);
            req.setAttribute("currentPage", pagination.getCurrentPage());
            req.setAttribute("totalPages", pagination.getTotalPages());
            req.getRequestDispatcher("/pages/orderHistory.jsp").forward(req, resp);
        }
    }

    private boolean isLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("loggedUser") != null;
    }

    private User getUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("loggedUser");
    }
}
