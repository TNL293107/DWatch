package servlet;

import dao.OrderDAO;
import model.Order;
import model.OrderDetail;
import model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/orders")
public class OrderHistoryServlet extends HttpServlet {

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
            List<Order> orders = orderDAO.getOrdersByEmail(user.getEmail());
            req.setAttribute("orders", orders);
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
